package cn.kavier.canal.adapter;

import cn.kavier.canal.adapter.properties.CanalAdapterProperties;
import com.alibaba.otter.canal.client.CanalConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAdapter implements Adapter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected CanalConnector connector;
    protected CanalAdapterProperties canalAdapterProperties;

    public AbstractAdapter(CanalAdapterProperties adapter) {
        this.canalAdapterProperties = adapter;
    }

    @Override
    public void init() {
        if (canalAdapterProperties.getThreadPool().getEnable()) {
            // init thread pool
        } else {
            // handle by single thread
            new Thread(this::etl).start();
        }
    }

    @Override
    public void etl() {

    }

    @Override
    public void destroy() {
        connector.disconnect();
    }
}
