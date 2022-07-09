package cn.kavier.canal.adapter.simple;

import cn.kavier.canal.adapter.properties.CanalAdapterProperties;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import cn.kavier.canal.adapter.AbstractAdapter;

import java.net.InetSocketAddress;
import java.util.List;

public class SimpleAdapter extends AbstractAdapter {

    public SimpleAdapter(CanalAdapterProperties adapter) {
        super(adapter);
    }

    @Override
    public void init() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(canalAdapterProperties.getServer().getHost(), canalAdapterProperties.getServer().getPort());
        String destination = canalAdapterProperties.getServer().getDestination();
        String username = canalAdapterProperties.getServer().getUsername();
        String password = canalAdapterProperties.getServer().getPassword();
        connector = CanalConnectors.newSingleConnector(inetSocketAddress, destination, username, password);
        super.init();
    }

    @Override
    public void etl() {
        Integer batchSizeConfig = canalAdapterProperties.getBatchSize();
        int batchSize = batchSizeConfig == null ? 1000 : batchSizeConfig <= 0 ? 1000 : batchSizeConfig;
        int emptyCount = 0;
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
        int totalEmptyCount = 120;
        while (emptyCount < totalEmptyCount) {
            Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
            long batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
                // nothing to do
                emptyCount++;
                System.out.println("empty count : " + emptyCount);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } else {
                emptyCount = 0;
                // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                printEntry(message.getEntries());
            }

            connector.ack(batchId); // 提交确认
            // connector.rollback(batchId); // 处理失败, 回滚数据
        }

        System.out.println("empty too many times, exit");
    }

    private static void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                // handle data change todo 识别是哪个数据表的数据，将对应数据库表的处理类进行实例化，然后执行
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}
