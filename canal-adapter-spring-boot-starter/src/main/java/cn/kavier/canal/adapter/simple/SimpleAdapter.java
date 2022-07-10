package cn.kavier.canal.adapter.simple;

import cn.kavier.canal.adapter.AbstractAdapter;
import cn.kavier.canal.adapter.properties.CanalAdapterProperties;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.net.InetSocketAddress;

public class SimpleAdapter extends AbstractAdapter {

    public SimpleAdapter(CanalAdapterProperties adapter) {
        super(adapter);
    }

    @Override
    public void init() {
        InetSocketAddress addr = new InetSocketAddress(canalAdapterProperties.getServer().getHost(),
                canalAdapterProperties.getServer().getPort());
        String destination = canalAdapterProperties.getServer().getDestination();
        String username = canalAdapterProperties.getServer().getUsername();
        String password = canalAdapterProperties.getServer().getPassword();
        connector = CanalConnectors.newSingleConnector(addr, destination, username, password);
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
        isConnected = true;
        super.init();
    }
}
