package cn.kavier.canal.adapter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = CanalAdapterProperties.PREFIX)
public class CanalAdapterProperties {

    public static final String PREFIX = "canal.adapter";

    private Boolean enable;
    private String connector;
    private Integer batchSize;
    private CanalServerProperties server;
    private CanalThreadPoolProperties threadPool;

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

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public CanalServerProperties getServer() {
        return server;
    }

    public void setServer(CanalServerProperties server) {
        this.server = server;
    }

    public CanalThreadPoolProperties getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(CanalThreadPoolProperties threadPool) {
        this.threadPool = threadPool;
    }
}
