package cn.joey.canal.adapter.configuration;

import cn.joey.canal.adapter.*;
import cn.joey.canal.adapter.properties.AdapterProperties;
import cn.joey.canal.adapter.properties.CanalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({CanalProperties.class})
@ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "enable", havingValue = "true")
public class CanalAdapterAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CanalAdapterAutoConfiguration.class);

    private final ApplicationContext applicationContext;

    private final CanalProperties canalProperties;

    public CanalAdapterAutoConfiguration(ApplicationContext applicationContext, CanalProperties canalProperties) {
        this.applicationContext = applicationContext;
        this.canalProperties = canalProperties;
    }

    @Bean(initMethod = "initialize")
    public HandlerMapping canalAdapterHandlerMapping() {
        return new RowHandlerMapping(applicationContext, canalProperties);
    }

    @DependsOn("canalAdapterHandlerMapping")
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "client.connect-mode", havingValue = "simple")
    public Adapter simpleAdapter(HandlerMapping handlerMapping) {
        return new SimpleAdapter(canalProperties, handlerMapping);
    }

    @DependsOn("canalAdapterHandlerMapping")
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "client.connect-mode", havingValue = "cluster")
    public Adapter clusterAdapter(HandlerMapping handlerMapping) {
        return new ClusterAdapter(canalProperties, handlerMapping);
    }

    @Bean
    @ConditionalOnProperty(prefix = CanalProperties.PREFIX, name = "adapter.thread-pool.enable", havingValue = "true")
    public ExecutorService canalAdapterExecutor(AbstractAdapter abstractAdapter) {
        AdapterProperties.ThreadPool threadPool = canalProperties.getAdapter().getThreadPool();
        Integer coreSize = threadPool.getCoreSize();
        Integer maxSize = threadPool.getMaxSize();
        Integer queueCapacity = threadPool.getQueueCapacity();
        ThreadPoolExecutorFactoryBean factory = new ThreadPoolExecutorFactoryBean();
        factory.setThreadNamePrefix("canal-adapter-thread-");
        logger.debug("初始化canal-adapter工作线程池，properties={}", threadPool);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize, threadPool.getKeepAliveTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(queueCapacity), factory);
        abstractAdapter.setExecutor(executor);
        return executor;
    }

}
