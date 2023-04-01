package cn.joey.canal.adapter.properties;


import cn.joey.canal.adapter.enums.CanalConnectModeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = CanalProperties.PREFIX)
public class CanalProperties {

    public static final String PREFIX = "canal";

    private Boolean enable;

    private Server server;

    private Client client;

    @NestedConfigurationProperty
    private AdapterProperties adapter;

    public Boolean isEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AdapterProperties getAdapter() {
        return adapter;
    }

    public void setAdapter(AdapterProperties adapter) {
        this.adapter = adapter;
    }

    @Component
    public static class Server {
        private String host;
        private String zkServers;
        private Integer port;
        private String username;
        private String password;
        private String destination;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getZkServers() {
            return zkServers;
        }

        public void setZkServers(String zkServices) {
            this.zkServers = zkServices;
        }
    }

    @Component
    public static class Client {
        private CanalConnectModeEnum connectMode;
        private String subscribe;
        private Integer batchSize = 1000;

        public CanalConnectModeEnum getConnectMode() {
            return connectMode;
        }

        public void setConnectMode(CanalConnectModeEnum connectMode) {
            this.connectMode = connectMode;
        }

        public String getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }

        public Integer getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(Integer batchSize) {
            this.batchSize = batchSize;
        }

    }


}
