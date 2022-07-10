package cn.kavier.canal.adapter;

import cn.kavier.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.kavier.canal.adapter.filter.AbstractFilter;
import cn.kavier.canal.adapter.loader.FilterLoader;
import cn.kavier.canal.adapter.properties.CanalAdapterProperties;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected CanalConnector connector;
    protected CanalAdapterProperties canalAdapterProperties;
    protected ExecutorService executor;
    protected volatile boolean isConnected;

    @Resource
    private FilterLoader filterLoader;

    public AbstractAdapter(CanalAdapterProperties adapter) {
        this.canalAdapterProperties = adapter;
    }

    @Override
    public void init() {
        if (canalAdapterProperties.getThreadPool().getEnable()) {
            Integer poolSize = canalAdapterProperties.getThreadPool().getMaxSize();
            log.info("创建线程池，大小={}", poolSize);
            executor = Executors.newFixedThreadPool(poolSize, new ThreadPoolExecutorFactoryBean());
            for (int i = 0; i < poolSize; i++) {
                executor.execute(new Thread(this::etl));
            }
        } else {
            new Thread(this::etl).start();
        }
    }

    @Override
    public void etl() {
        Integer batchSizeConfig = canalAdapterProperties.getBatchSize();
        int batchSize = batchSizeConfig == null ? 1000 : batchSizeConfig <= 0 ? 1000 : batchSizeConfig;
        while (isConnected) {
            Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
            long batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
                connector.ack(batchId);
            } else {
                try {
                    System.out.println("线程消费，" + Thread.currentThread().getName());
                    log.info("message[batchId={},size={}]", batchId, size);
                    // todo 根据不同的表，调用对应的实现类filter 去处理数据
                    String name = "根据不同的tableName获取到配置好的filter名称";
                    AbstractFilter filter = filterLoader.getFilter(name);
                    filter.filter(message.getEntries());
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
