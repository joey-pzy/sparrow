package cn.kavier.canal.adapter.config;

import cn.kavier.canal.adapter.Adapter;
import cn.kavier.canal.adapter.enums.CanalConnectorEnum;
import cn.kavier.canal.adapter.exception.CanalAdapterException;
import cn.kavier.canal.adapter.filter.AbstractFilter;
import cn.kavier.canal.adapter.filter.DefaultFilter;
import cn.kavier.canal.adapter.properties.CanalAdapterConfig;
import cn.kavier.canal.adapter.properties.CanalAdaptersConfig;
import cn.kavier.canal.adapter.simple.SimpleAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@EnableConfigurationProperties(CanalAdapterConfig.class)
@ConditionalOnProperty(prefix = CanalAdapterConfig.PREFIX, name = "enable", havingValue = "true")
public class CanalClientAdapterAutoConfiguration {

    public static final String COMMA = ",";

    private final CanalAdapterConfig canalAdapterConfig;

    public CanalClientAdapterAutoConfiguration(CanalAdapterConfig canalAdapterConfig) {
        this.canalAdapterConfig = canalAdapterConfig;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public Adapter adapter() throws Exception {
        this.initTableFilterMap();

        String connector = canalAdapterConfig.getConnector();
        if (null == connector) {
            throw new CanalAdapterException(CanalAdapterConfig.PREFIX + ".connector must not be null");
        }
        try {
            switch (CanalConnectorEnum.valueOf(connector.toUpperCase())) {
                case SIMPLE:
                    return new SimpleAdapter(canalAdapterConfig);
                case CLUSTER:
                default:
                    throw new CanalAdapterException("config value of '" + CanalAdapterConfig.PREFIX + ".connector : " + connector + "' not support now");
            }
        } catch (IllegalArgumentException e) {
            throw new CanalAdapterException("config value of '" + CanalAdapterConfig.PREFIX + ".connector : " + connector + "' is wrong");
        }
    }

    void initTableFilterMap() throws CanalAdapterException {
        // 将 tableName -> filter 映射加入内存
        List<CanalAdaptersConfig.FilterConfig> filters = canalAdapterConfig.getAdapters().getFilters();
        Map<String, Set<AbstractFilter>> tableFilterMap = new HashMap<>();
        for (CanalAdaptersConfig.FilterConfig filter : filters) {
            if (null == filter.getTableName()) {
                throw new CanalAdapterException("filters.tableName is null");
            }
            for (String tableName : filter.getTableName().split(COMMA)) {
                Set<AbstractFilter> tf = tableFilterMap.get(tableName);
                if (tf != null) {
                    tf.add(new DefaultFilter());
                } else {
                    tf = new HashSet<>();
                    tf.add(new DefaultFilter());
                    tableFilterMap.put(tableName, tf);
                }
            }
        }
        canalAdapterConfig.getAdapters().setTableFilterMap(tableFilterMap);
    }
}
