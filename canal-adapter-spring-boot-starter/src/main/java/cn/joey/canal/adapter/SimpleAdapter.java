package cn.joey.canal.adapter;

import cn.joey.canal.adapter.properties.CanalProperties;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.net.InetSocketAddress;

public class SimpleAdapter extends AbstractAdapter {

    public SimpleAdapter(CanalProperties canalProperties) {
        super(canalProperties);
    }

    @Override
    public void start() {
        InetSocketAddress addr = new InetSocketAddress(serverProperties.getHost(), serverProperties.getPort());
        String destination = serverProperties.getDestination();
        String username = serverProperties.getUsername();
        String password = serverProperties.getPassword();
        super.connector = CanalConnectors.newSingleConnector(addr, destination, username, password);
        super.start();
    }
}
