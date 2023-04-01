package cn.joey.canal.adapter;

import cn.joey.canal.adapter.properties.CanalProperties;
import com.alibaba.otter.canal.client.CanalConnectors;

public class ClusterAdapter extends AbstractAdapter {

    public ClusterAdapter(CanalProperties canalProperties, HandlerMapping handlerMapping) {
        super(canalProperties, handlerMapping);
    }

    @Override
    public void start() {
        String destination = serverProperties.getDestination();
        String username = serverProperties.getUsername();
        String password = serverProperties.getPassword();
        super.connector = CanalConnectors.newClusterConnector(serverProperties.getZkServers(), destination, username, password);
        super.start();
    }
}
