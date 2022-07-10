package cn.kavier.canal.adapter;

import cn.kavier.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.kavier.canal.adapter.filter.AbstractFilter;
import cn.kavier.canal.adapter.properties.CanalAdapterConfig;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected CanalConnector connector;
    protected CanalAdapterConfig adapterConfig;
    protected ExecutorService executor;
    protected volatile boolean isConnected;

    public AbstractAdapter(CanalAdapterConfig adapter) {
        this.adapterConfig = adapter;
    }

    @Override
    public void init() {
        if (adapterConfig.getThreadPool().getEnable()) {
            Integer poolSize = adapterConfig.getThreadPool().getMaxSize();
            log.debug("创建线程池，大小={}", poolSize);
            executor = Executors.newFixedThreadPool(poolSize, new ThreadPoolExecutorFactoryBean());
            for (int i = 0; i < poolSize; i++) {
                // todo 多线程使用自动配置进行加载，并且每个线程注入的对象是新的对象，单例对象会有线程安全问题
                executor.execute(new Thread(this::etl));
            }
        } else {
            new Thread(this::etl).start();
        }
    }

    @Override
    public void etl() {
        Integer batchSizeConfig = adapterConfig.getBatchSize();
        int batchSize = batchSizeConfig == null ? 1000 : batchSizeConfig <= 0 ? 1000 : batchSizeConfig;
        while (isConnected) {
            Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
            long batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
                log.debug("nothing to do, sleep 1000 ms");
                connector.ack(batchId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.debug("thread wait exception");
                }
            } else {
                try {
                    log.debug("message[batchId={},size={}]", batchId, size);
                    for (CanalEntry.Entry entry : message.getEntries()) {
                        String tableName = entry.getHeader().getTableName();
                        Set<AbstractFilter> filters = adapterConfig.getAdapters().getFilters(tableName);
                        filters.forEach(i -> i.filter(entry));
                    }
                    connector.ack(batchId);
                } catch (CanalAdapterRuntimeException e) {
                    connector.rollback(batchId);
                    Thread.yield();
                }
            }
        }
    }

    @Override
    public void destroy() {
        connector.disconnect();
        isConnected = false;
        if (executor != null) {
            executor.shutdown();
        }
        log.info("canal adapter destroy");
    }
}
