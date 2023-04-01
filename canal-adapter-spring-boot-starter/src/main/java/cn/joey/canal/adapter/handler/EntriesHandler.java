package cn.joey.canal.adapter.handler;

import cn.joey.canal.adapter.HandlerMapping;
import cn.joey.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.joey.canal.adapter.handler.row.RowHandler;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EntriesHandler implements EntryHandler {

    private static final Logger logger = LoggerFactory.getLogger(EntriesHandler.class);

    private final HandlerMapping handlerMapping;

    public EntriesHandler(HandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void handle(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            this.handleEntry(entry);
        }
    }

    private void handleEntry(CanalEntry.Entry entry) {
        CanalEntry.RowChange rowChange;
        try {
            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            throw new CanalAdapterRuntimeException("ERROR ## parser of StoreValue has an error , data:" + entry, e);
        }

        CanalEntry.EventType eventType = rowChange.getEventType();
        CanalEntry.Header header = entry.getHeader();
        logger.debug("\nentry.header=\n{}", header);
        String tableName = header.getTableName();

        RowHandler rowHandler = handlerMapping.getRowHandler(tableName);
        if (rowHandler == null) {
            logger.warn("表 {} 没有对应的 CanalAdapter 注解修饰的处理类，已跳过消费", tableName);
            return;
        }
        if (eventType == CanalEntry.EventType.DELETE) {
            rowHandler.delete(rowChange.getRowDatasList());
        } else if (eventType == CanalEntry.EventType.INSERT) {
            rowHandler.insert(rowChange.getRowDatasList());
        } else if (eventType == CanalEntry.EventType.UPDATE) {
            rowHandler.update(rowChange.getRowDatasList());
        } else {
            logger.error("未实现事件 {} 的处理方式", eventType);
        }
    }
}
