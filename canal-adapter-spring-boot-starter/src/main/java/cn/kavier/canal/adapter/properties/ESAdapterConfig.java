package cn.kavier.canal.adapter.properties;

import cn.kavier.canal.adapter.filter.AbstractRowFilter;
import cn.kavier.canal.adapter.filter.DefaultRowFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author joey 2022-07-17
 */
@Component
@ConfigurationProperties(prefix = ESAdapterConfig.PREFIX)
public class ESAdapterConfig {

    public static final String PREFIX = CanalAdapterConfig.PREFIX + ".es-adapter";

    private List<FilterConfig> filters;
    private Map<String, Set<AbstractRowFilter>> tableFilterMap;

    public Set<AbstractRowFilter> getFilters(String tableName) {
        Set<AbstractRowFilter> filters = tableFilterMap.get(tableName);
        if (null != filters && filters.size() > 0) {
            return filters;
        }
        filters = new HashSet<>(1);
        filters.add(new DefaultRowFilter());
        return filters;
    }

    public List<FilterConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterConfig> filters) {
        this.filters = filters;
    }

    public Map<String, Set<AbstractRowFilter>> getTableFilterMap() {
        return tableFilterMap;
    }

    public void setTableFilterMap(Map<String, Set<AbstractRowFilter>> tableFilterMap) {
        this.tableFilterMap = tableFilterMap;
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
