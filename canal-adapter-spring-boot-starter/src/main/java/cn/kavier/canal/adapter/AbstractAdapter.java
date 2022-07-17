package cn.kavier.canal.adapter;

import cn.kavier.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.kavier.canal.adapter.filter.AbstractRowFilter;
import cn.kavier.canal.adapter.properties.CanalAdapterConfig;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected CanalConnector connector;

    protected volatile boolean isConnected;

    protected CanalAdapterConfig adapterConfig;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    protected ExecutorService executor;

    public AbstractAdapter(CanalAdapterConfig adapter) {
        this.adapterConfig = adapter;
    }

    @Override
    public void start() {
        connector.connect();
        connector.subscribe(adapterConfig.getSubscribe());
        connector.rollback();
        isConnected = true;

        Thread adapterThread = new Thread(this::etl);
        adapterThread.setName("canal-adapter-thread-main");
        adapterThread.start();
    }

    @Override
    public void etl() {
        Integer batchSizeConfig = adapterConfig.getBatchSize();
        int batchSize = batchSizeConfig == null ? 1000 : batchSizeConfig <= 0 ? 1000 : batchSizeConfig;
        while (isConnected) {
            // todo 这里统一处理连接异常等情况
            // todo 增加重试机制
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();
            int size = message.getEntries().size();

            if (batchId != -1 && size != 0) {
                log.debug("message[batchId={},size={}]", batchId, size);
                if (executor != null) {
                    // 多线程处理
                    // todo 考虑怎么处理ask
                    Thread thread = new Thread(() -> {
                        log.debug("线程处理message，线程id={}", Thread.currentThread().getName());
                        for (CanalEntry.Entry entry : message.getEntries()) {
                            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                                continue;
                            }
                            String tableName = entry.getHeader().getTableName();
                            Set<AbstractRowFilter> filters = adapterConfig.getEsAdapter().getFilters(tableName);
                            filters.forEach(i -> i.filter(entry));
                        }
                        log.debug("消息消费完成，不ask，睡30000 ms");
                        try {
                            Thread.sleep(300000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.setName("canal-adapter-thread-" + Math.random() * 10);
                    executor.execute(thread);
                } else {
                    // 单线程处理
                    try {
                        for (CanalEntry.Entry entry : message.getEntries()) {
                            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                                continue;
                            }
                            String tableName = entry.getHeader().getTableName();
                            Set<AbstractRowFilter> filters = adapterConfig.getEsAdapter().getFilters(tableName);
                            filters.forEach(i -> i.filter(entry));
                        }
                        connector.ack(batchId);
                    } catch (CanalAdapterRuntimeException e) {
                        connector.rollback(batchId);
                    }
                }
            }

            try {
                log.debug("nothing to do, sleep 1000 ms");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.debug("thread wait exception");
            }
        }
    }

    @Override
    public void destroy() {
        connector.unsubscribe();
        connector.disconnect();
        isConnected = false;
        if (executor != null) {
            executor.shutdown();
        }
        log.info("canal adapter destroy");
    }
}
