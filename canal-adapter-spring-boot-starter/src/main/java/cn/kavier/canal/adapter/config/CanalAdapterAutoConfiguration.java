package cn.kavier.canal.adapter.config;

import cn.kavier.canal.adapter.Adapter;
import cn.kavier.canal.adapter.enums.CanalConnectModeEnum;
import cn.kavier.canal.adapter.exception.CanalAdapterException;
import cn.kavier.canal.adapter.filter.AbstractRowFilter;
import cn.kavier.canal.adapter.filter.DefaultRowFilter;
import cn.kavier.canal.adapter.properties.AdapterThreadPoolConfig;
import cn.kavier.canal.adapter.properties.CanalAdapterConfig;
import cn.kavier.canal.adapter.properties.ESAdapterConfig;
import cn.kavier.canal.adapter.simple.SimpleAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(CanalAdapterConfig.class)
@ConditionalOnProperty(prefix = CanalAdapterConfig.PREFIX, name = "enable", havingValue = "true")
public class CanalAdapterAutoConfiguration {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String COMMA = ",";

    private final CanalAdapterConfig canalAdapterConfig;

    public CanalAdapterAutoConfiguration(CanalAdapterConfig canalAdapterConfig) {
        this.canalAdapterConfig = canalAdapterConfig;
    }

    @Bean
    @ConditionalOnProperty(prefix = AdapterThreadPoolConfig.PREFIX, name = "enable", havingValue = "true")
    public ExecutorService adapterExecutor() {
        Integer maxSize = canalAdapterConfig.getThreadPool().getMaxSize();
        log.debug("创建线程池，maxSize={}", maxSize);
        return Executors.newFixedThreadPool(maxSize, new ThreadPoolExecutorFactoryBean());
    }

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public Adapter adapter() throws Exception {
        this.initTableFilterMap();

        String connectMode = canalAdapterConfig.getConnectMode();
        if (null == connectMode) {
            throw new CanalAdapterException(CanalAdapterConfig.PREFIX + ".connect-mode must not be null");
        }
        try {
            switch (CanalConnectModeEnum.valueOf(connectMode.toUpperCase(Locale.ROOT))) {
                case SIMPLE:
                    return new SimpleAdapter(canalAdapterConfig);
                case CLUSTER:
                default:
                    throw new CanalAdapterException("config value of '" + CanalAdapterConfig.PREFIX + ".connect-mode : " + connectMode + "' not support now");
            }
        } catch (IllegalArgumentException e) {
            throw new CanalAdapterException("config value of '" + CanalAdapterConfig.PREFIX + ".connect-mode : " + connectMode + "' is wrong");
        }
    }

    private void initTableFilterMap() {
        // 将 tableName -> filter 映射加入内存
        List<ESAdapterConfig.FilterConfig> filters = canalAdapterConfig.getEsAdapter().getFilters();
        Map<String, Set<AbstractRowFilter>> tableFilterMap = new HashMap<>();

        for (ESAdapterConfig.FilterConfig filter : filters) {
            if (null != filter.getTableName()) {
                for (String tableName : filter.getTableName().split(COMMA)) {
                    if (!tableName.isEmpty()) {
                        Set<AbstractRowFilter> tf = tableFilterMap.get(tableName);
                        // todo 读取配置，从容器中将对应的自定义过滤类获取到，放进这个map中
                        if (tf != null) {
                            tf.add(new DefaultRowFilter());
                        } else {
                            tf = new HashSet<>();
                            tf.add(new DefaultRowFilter());
                            tableFilterMap.put(tableName, tf);
                        }
                    }
                }
            }
        }
        canalAdapterConfig.getEsAdapter().setTableFilterMap(tableFilterMap);
    }

}
