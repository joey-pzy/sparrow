package cn.kavier.canal.adapter.properties;

import cn.kavier.canal.adapter.filter.AbstractFilter;
import cn.kavier.canal.adapter.filter.DefaultFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = CanalAdaptersConfig.PREFIX)
public class CanalAdaptersConfig {

    public static final String PREFIX = CanalAdapterConfig.PREFIX + ".adapters";

    private List<FilterConfig> filters;
    private Map<String, Set<AbstractFilter>> tableFilterMap;

    public Set<AbstractFilter> getFilters(String tableName) {
        Set<AbstractFilter> filters = tableFilterMap.get(tableName);
        if (null != filters && filters.size() > 0) {
            return filters;
        }
        filters = new HashSet<>(1);
        filters.add(new DefaultFilter());
        return filters;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public Map<String, Set<AbstractFilter>> getTableFilterMap() {
        return tableFilterMap;
    }

    public void setTableFilterMap(Map<String, Set<AbstractFilter>> tableFilterMap) {
        this.tableFilterMap = tableFilterMap;
    }

    @Component
    @ConfigurationProperties(prefix = CanalAdaptersConfig.PREFIX + ".filters")
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
