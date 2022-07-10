package cn.kavier.canal.adapter.simple;

import cn.kavier.canal.adapter.AbstractAdapter;
import cn.kavier.canal.adapter.properties.CanalAdapterConfig;
import cn.kavier.canal.adapter.properties.CanalServerConfig;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.net.InetSocketAddress;

public class SimpleAdapter extends AbstractAdapter {

    public SimpleAdapter(CanalAdapterConfig adapter) {
        super(adapter);
    }

    @Override
    public void init() {
        CanalServerConfig serverConfig = adapterConfig.getServer();
        InetSocketAddress addr = new InetSocketAddress(serverConfig.getHost(), serverConfig.getPort());
        String destination = adapterConfig.getServer().getDestination();
        String username = adapterConfig.getServer().getUsername();
        String password = adapterConfig.getServer().getPassword();
        connector = CanalConnectors.newSingleConnector(addr, destination, username, password);
        connector.connect();
        connector.subscribe(adapterConfig.getSubscribe());
        connector.rollback();
        isConnected = true;
        super.init();
    }
}
