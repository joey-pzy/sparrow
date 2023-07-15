package ink.joey.web.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean("datasource")
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("otherDatasource")
    public Map<String, DataSource> otherDataSource(OtherDatasourceProperties otherDatasourceProperties) {
        List<DataSourceProperties> properties = otherDatasourceProperties.getOtherDatasource();
        Map<String, DataSource> dataSourcesMap = new HashMap<>(properties.size());
        properties.forEach(property -> {
            HikariConfig config = new HikariConfig();
            config.setPoolName(property.getName() + "-Hikari Pool");
            config.setDriverClassName(property.getDriverClassName());
            config.setJdbcUrl(property.getUrl());
            config.setUsername(property.getUsername());
            config.setPassword(property.getPassword());
            dataSourcesMap.put(property.getName(), new HikariDataSource(config));
        });
        return dataSourcesMap;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("otherDatasource") Map<String, DataSource> otherDatasource) {
        Map<Object, Object> sourceMap = new HashMap<>();
        sourceMap.put("master", this.dataSource());
        sourceMap.putAll(otherDatasource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(this.dataSource());
        dynamicDataSource.setTargetDataSources(sourceMap);

        return dynamicDataSource;
    }
}
