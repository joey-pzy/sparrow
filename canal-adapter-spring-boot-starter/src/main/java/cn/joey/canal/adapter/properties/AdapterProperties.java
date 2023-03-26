package cn.joey.canal.adapter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author joey 2022-07-17
 */
@Component
@ConfigurationProperties(prefix = AdapterProperties.PREFIX)
public class AdapterProperties {

    public static final String PREFIX = CanalProperties.PREFIX + ".adapter";

    private ThreadPool threadPool;

    private List<FilterConfig> filters;

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * 工作线程池配置
     */
    @Component
    public static class ThreadPool {
        private Boolean enable = false;
        private Integer coreSize = 1;
        private Integer maxSize = 1;
        private Integer queueCapacity = 10;

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public Integer getCoreSize() {
            return coreSize;
        }

        public void setCoreSize(Integer coreSize) {
            this.coreSize = coreSize;
        }

        public Integer getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(Integer maxSize) {
            this.maxSize = maxSize;
        }

        public Integer getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        @Override
        public String toString() {
            return "{" +
                    "enable=" + enable +
                    ", coreSize=" + coreSize +
                    ", maxSize=" + maxSize +
                    ", queueCapacity=" + queueCapacity +
                    '}';
        }
    }

    /**
     * 数据过滤组配置
     */
    @Component
    public static class FilterConfig {
        private String key;
        private String tableName;
        private String esIndexName;
        private String filter;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getEsIndexName() {
            return esIndexName;
        }

        public void setEsIndexName(String esIndexName) {
            this.esIndexName = esIndexName;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }
    }

}
