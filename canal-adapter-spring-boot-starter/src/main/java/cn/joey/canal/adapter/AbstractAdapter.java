package cn.joey.canal.adapter;

import cn.joey.canal.adapter.exception.AdapterRunningStatusException;
import cn.joey.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.joey.canal.adapter.filter.AbstractRowFilter;
import cn.joey.canal.adapter.loader.FilterLoader;
import cn.joey.canal.adapter.properties.CanalProperties;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Thread adapterThread = null;

    protected Thread.UncaughtExceptionHandler handler = (t, e) -> log.error("数据同步发生未知异常", e);

    protected CanalConnector connector;

    protected volatile boolean running;

    protected CanalProperties.Server serverProperties;

    protected CanalProperties.Client clientProperties;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    protected ExecutorService executor;

    @Resource
    private FilterLoader filterLoader;

    public AbstractAdapter(CanalProperties canalProperties) {
        this.clientProperties = canalProperties.getClient();
        this.serverProperties = canalProperties.getServer();
    }

    @Override
    public void start() {
        if (running) {
            throw new AdapterRunningStatusException("is running yet");
        }
        if (null == connector) {
            throw new CanalAdapterRuntimeException("connector is null");
        }
        adapterThread = new Thread(this::process);
        adapterThread.setName("canal-adapter-thread-main");
        adapterThread.setUncaughtExceptionHandler(handler);
        running = true;
        adapterThread.start();
    }

    @Override
    public void process() {
        Integer batchSizeConfig = clientProperties.getBatchSize();
        int batchSize = batchSizeConfig == null ? 1000 : batchSizeConfig <= 0 ? 1000 : batchSizeConfig;

        if (running) {
            int retry = 3;
            boolean connectSuccessfully = false;
            do {
                try {
                    connector.connect();
                    connector.subscribe(clientProperties.getSubscribe());
                    connector.rollback();
                    connectSuccessfully = true;
                } catch (CanalClientException var1) {
                    log.error("Adapter 尝试连接 canal server 发生异常，10s 后将重试连接，请检查网络是否顺畅，或检查 canal server 是否启动", var1);
                    try {
                        // 连接异常，等待10s后继续重试
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    retry--;
                }
            } while (!connectSuccessfully && retry > 0);

            if (!connectSuccessfully) {
                throw new CanalAdapterRuntimeException("连接 canal server 失败");
            }
        }

        try {
            while (running) {
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();

                // todo 遇见批次异常，是直接抛出异常中断整个同步，还是跳过？
                if (batchId != -1 && size != 0) {
                    log.debug("message[batchId={},size={}]", batchId, size);
                    // todo 在这里不进行循环，每一批次都交给一个新线程执行，且后续的每次执行都要创建新的数据对象，让其线程私有，这样保证线程安全
                    for (CanalEntry.Entry entry : message.getEntries()) {
                        if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                            continue;
                        }
                        // todo 这里考虑先使用 handle 处理数据，然后才是自定义 filter/conversion 进行数据过滤或者格式转换
                        Set<AbstractRowFilter> filters = filterLoader.loadFilters(entry.getHeader().getTableName());
                        filters.forEach(i -> i.filter(entry));
                    }
                    // todo 异步的情况需要获取到线程执行结果，才进行ask，线程异常则根据【配置】设定直接阻断还是跳过
                    connector.ack(batchId);
                } else {
                    log.info("nothing to do...wait 3000 ms");
                    Thread.sleep(3000);
                }

            }
        } catch (Throwable e) {
            log.error("数据同步异常", e);
            connector.rollback();
        } finally {
            //connector.unsubscribe();
            connector.disconnect();
            log.info("已断开与 canal server 的连接");
        }
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        log.info("canal adapter destroy");
    }
}
