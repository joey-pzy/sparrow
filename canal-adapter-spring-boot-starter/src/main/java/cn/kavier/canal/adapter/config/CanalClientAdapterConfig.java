package cn.kavier.canal.adapter.config;

import cn.kavier.canal.adapter.Adapter;
import cn.kavier.canal.adapter.enums.CanalConnectorEnum;
import cn.kavier.canal.adapter.exception.CanalAdapterException;
import cn.kavier.canal.adapter.properties.CanalAdapterProperties;
import cn.kavier.canal.adapter.simple.SimpleAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CanalAdapterProperties.class})
@ConditionalOnProperty(prefix = CanalAdapterProperties.PREFIX, name = "enable", havingValue = "true")
public class CanalClientAdapterConfig {

    private final CanalAdapterProperties canalAdapterProperties;

    public CanalClientAdapterConfig(CanalAdapterProperties canalAdapterProperties) {
        this.canalAdapterProperties = canalAdapterProperties;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public Adapter esAdapter() throws Exception {
        String connector = canalAdapterProperties.getConnector();
        if (null == connector) {
            throw new CanalAdapterException(CanalAdapterProperties.PREFIX + ".connector must not be null");
        }
        try {
            switch (CanalConnectorEnum.valueOf(connector.toUpperCase())) {
                case SIMPLE:
                    return new SimpleAdapter(canalAdapterProperties);
                case CLUSTER:
                default:
                    throw new CanalAdapterException("config value of '" + CanalAdapterProperties.PREFIX + ".connector : " + connector + "' not support now");
            }
        } catch (IllegalArgumentException e) {
            throw new CanalAdapterException("config value of '" + CanalAdapterProperties.PREFIX + ".connector : " + connector + "' is wrong");
        }
    }

}
