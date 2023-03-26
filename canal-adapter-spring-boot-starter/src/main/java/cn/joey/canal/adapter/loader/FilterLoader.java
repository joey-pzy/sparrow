package cn.joey.canal.adapter.loader;

import cn.joey.canal.adapter.filter.AbstractRowFilter;
import cn.joey.canal.adapter.filter.DefaultRowFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FilterLoader {

    @Resource(name = "tableFilterMap")
    private Map<String, Set<AbstractRowFilter>> tableFilterMap;

    private Set<AbstractRowFilter> defaultFilters;

    public Set<AbstractRowFilter> loadFilters(String tableName) {
        Set<AbstractRowFilter> filters = tableFilterMap.get(tableName);
        if (null == filters || filters.isEmpty()) {
            return this.loadDefaultFilters();
        }
        return filters;
    }

    private Set<AbstractRowFilter> loadDefaultFilters() {
        if (null == defaultFilters) {
            defaultFilters = new HashSet<>(1);
            defaultFilters.add(new DefaultRowFilter());
        }
        return defaultFilters;
    }
}
