package cn.kavier.canal.adapter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = ThreadPoolProperties.PREFIX)
public class ThreadPoolProperties {

    public static final String PREFIX = CanalAdapterProperties.PREFIX + ".thread-pool";

    private Boolean enable = false;
    private Integer maxSize = 1;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}
