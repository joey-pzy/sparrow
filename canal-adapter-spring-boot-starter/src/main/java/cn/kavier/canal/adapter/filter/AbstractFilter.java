package cn.kavier.canal.adapter.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    public final void filter(CanalEntry.Entry entry) {
        this.beforeProcess(entry);
        this.process();
        this.afterProcess();
    }

    @Override
    public void beforeProcess(CanalEntry.Entry entry) {
        printEntry(entry);
    }

    void printEntry(CanalEntry.Entry entry) {
        if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
            return;
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
                processDelete();
                printColumn(rowData.getBeforeColumnsList());
            } else if (eventType == CanalEntry.EventType.INSERT) {
                processInsert();
                printColumn(rowData.getAfterColumnsList());
            } else {
                processUpdate();
                System.out.println("-------&gt; before");
                printColumn(rowData.getBeforeColumnsList());
                System.out.println("-------&gt; after");
                printColumn(rowData.getAfterColumnsList());
            }
        }
    }

    void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    @Override
    public void process() {

    }

    public void processInsert() {
        log.info("insert ===>");
        this.afterProcess();
    }

    public void processDelete() {
        log.info("delete ===>");

        this.afterProcess();
    }

    public void processUpdate() {
        log.info("update ===>");

        this.afterProcess();
    }

    @Override
    public void afterProcess() {
        log.info("afterProcess ===>");

    }
}
