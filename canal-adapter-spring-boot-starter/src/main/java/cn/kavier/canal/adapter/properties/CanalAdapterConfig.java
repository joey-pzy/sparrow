package cn.kavier.canal.adapter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = CanalAdapterConfig.PREFIX)
public class CanalAdapterConfig {

    public static final String PREFIX = "canal-adapter";

    private Boolean enable;
    private String connectMode;
    private String subscribe;
    private Integer batchSize;
    private CanalServerConfig server;
    private AdapterThreadPoolConfig threadPool;
    private ESAdapterConfig esAdapter;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
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

    public ESAdapterConfig getEsAdapter() {
        return esAdapter;
    }

    public void setEsAdapter(ESAdapterConfig esAdapter) {
        this.esAdapter = esAdapter;
    }
}
