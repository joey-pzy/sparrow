package cn.kavier.canal.adapter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = CanalAdapterConfig.PREFIX)
public class CanalAdapterConfig {

    public static final String PREFIX = "canal.adapter";

    private Boolean enable;
    private String connector;
    private String subscribe;
    private Integer batchSize;
    private CanalServerConfig server;
    private AdapterThreadPoolConfig threadPool;
    private CanalAdaptersConfig adapters;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public CanalServerConfig getServer() {
        return server;
    }

    public void setServer(CanalServerConfig server) {
        this.server = server;
    }

    public AdapterThreadPoolConfig getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(AdapterThreadPoolConfig threadPool) {
        this.threadPool = threadPool;
    }

    public CanalAdaptersConfig getAdapters() {
        return adapters;
    }

    public void setAdapters(CanalAdaptersConfig adapters) {
        this.adapters = adapters;
    }
}
