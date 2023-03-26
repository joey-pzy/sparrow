package cn.joey.canal.adapter.config;

import cn.joey.canal.adapter.Adapter;
import cn.joey.canal.adapter.SimpleAdapter;
import cn.joey.canal.adapter.filter.AbstractRowFilter;
import cn.joey.canal.adapter.filter.DefaultRowFilter;
import cn.joey.canal.adapter.properties.AdapterProperties;
import cn.joey.canal.adapter.properties.CanalProperties;
import cn.joey.canal.adapter.template.ESTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({CanalProperties.class})
@ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "enable", havingValue = "true")
public class CanalAdapterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CanalAdapterAutoConfiguration.class);

    private static final String COMMA;

    static {
        COMMA = ",";
    }

    private final ApplicationContext applicationContext;

    private final CanalProperties canalProperties;

    public CanalAdapterAutoConfiguration(ApplicationContext applicationContext, CanalProperties canalProperties) {
        this.applicationContext = applicationContext;
        this.canalProperties = canalProperties;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "client.connect-mode", havingValue = "simple")
    public Adapter adapter() throws Exception {
        return new SimpleAdapter(canalProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "adapter.thread-pool.enable", havingValue = "true")
    public ExecutorService adapterExecutor() {
        AdapterProperties.ThreadPool threadPool = canalProperties.getAdapter().getThreadPool();
        Integer coreSize = threadPool.getCoreSize();
        Integer maxSize = threadPool.getMaxSize();
        Integer queueCapacity = threadPool.getQueueCapacity();
        ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
        factory.setThreadNamePrefix("canal-adapter-thread-");
        log.debug("初始化canal-adapter工作线程池，properties={}", threadPool);
        return new ThreadPoolExecutor(coreSize, maxSize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueCapacity), factory);
    }

    @Bean
    public Map<String, Set<AbstractRowFilter>> tableFilterMap() {
        List<AdapterProperties.FilterConfig> filters = canalProperties.getAdapter().getFilters();
        Map<String, Set<AbstractRowFilter>> tableFilterMap = new HashMap<>();
        for (AdapterProperties.FilterConfig filter : filters) {
            if (null != filter.getTableName() && !filter.getTableName().isEmpty()) {
                for (String tableName : filter.getTableName().split(COMMA)) {
                    if (null != tableName && !tableName.isEmpty()) {
                        Set<AbstractRowFilter> tf = tableFilterMap.get(tableName);
                        if (tf != null) {
                            if (null != filter.getFilter() && !filter.getFilter().isEmpty()) {
                                tf.add((AbstractRowFilter) applicationContext.getBean(filter.getFilter()));
                            } else {
                                tf.add(new DefaultRowFilter(filter.getEsIndexName()));
                            }
                        } else {
                            tf = new HashSet<>();
                            if (null != filter.getFilter() && !filter.getFilter().isEmpty()) {
                                tf.add((AbstractRowFilter) applicationContext.getBean(filter.getFilter()));
                            } else {
                                tf.add(new DefaultRowFilter(filter.getEsIndexName()));
                            }
                            tableFilterMap.put(tableName, tf);
                        }
                    }
                }
            }
        }
        return tableFilterMap;
    }

    @Bean
    public ESTemplate esTemplate() {
        return new ESTemplate();
    }
}
