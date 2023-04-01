package cn.joey.canal.adapter;

import cn.joey.canal.adapter.exception.AdapterRunningStatusException;
import cn.joey.canal.adapter.handler.EntriesHandler;
import cn.joey.canal.adapter.properties.CanalProperties;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Thread adapterThread = null;

    protected Thread.UncaughtExceptionHandler handler = (t, e) -> logger.error("数据消费线程未处理异常", e);

    protected CanalConnector connector;

    protected volatile boolean running;

    protected CanalProperties.Server serverProperties;

    protected CanalProperties.Client clientProperties;

    protected boolean rollbackWhenException;

    private final HandlerMapping handlerMapping;

    private Executor executor;

    protected AbstractAdapter(CanalProperties canalProperties, HandlerMapping handlerMapping) {
        this.clientProperties = canalProperties.getClient();
        this.serverProperties = canalProperties.getServer();
        this.handlerMapping = handlerMapping;
        this.rollbackWhenException = canalProperties.getAdapter().getRollbackWhenException();
    }

    @Override
    public void start() {
        if (running) {
            throw new AdapterRunningStatusException("is running yet");
        }
        adapterThread = new Thread(this::process, "canal-adapter-thread-main");
        adapterThread.setUncaughtExceptionHandler(handler);
        running = true;
        adapterThread.start();
        logger.info("canal adapter started ...");
    }

    private void connect() {
        boolean connectSuccessfully = false;
        do {
            try {
                connector.connect();
                connector.subscribe(clientProperties.getSubscribe());
                connector.rollback();
                connectSuccessfully = true;
            } catch (CanalClientException var1) {
                logger.error("Adapter 尝试连接 canal server 发生异常，请检查网络是否顺畅，或检查 canal server 是否启动", var1);
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (running && !connectSuccessfully);
    }

    @Override
    public void process() {
        Integer batchSizeConfig = clientProperties.getBatchSize();
        int batchSize = batchSizeConfig <= 0 ? 1000 : batchSizeConfig;
        connect();
        try {
            EntriesHandler entriesHandler = new EntriesHandler(handlerMapping);
            while (running) {
                long batchId = -1;
                try {
                    Message message = connector.getWithoutAck(batchSize);
                    batchId = message.getId();
                    int size = message.getEntries().size();

                    if (batchId != -1 && size != 0) {
                        logger.debug("message[batchId={},size={}]", batchId, size);
                        if (this.executor != null) {
                            executor.execute(() -> entriesHandler.handle(message.getEntries()));
                        } else {
                            entriesHandler.handle(message.getEntries());
                        }
                        connector.ack(batchId);
                    }

                } catch (Exception e) {
                    if (rollbackWhenException) {
                        connector.rollback();
                        logger.info("数据消费异常，已回滚，batchId={}", batchId, e);
                        // 当异常需要回滚时，直接退出数据处理，因为继续执行也是一直异常
                        break;
                    } else {
                        // todo 最好在这里记录下所有异常数据，从而提供给数据补偿机制
                        logger.info("数据消费异常，已跳过该批次ID，batchId={}", batchId, e);
                    }
                }
                Thread.yield();
            }
        } finally {
            connector.disconnect();
            logger.info("已断开与 canal server 的连接");
        }
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        logger.info("canal adapter stopped");
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
}
