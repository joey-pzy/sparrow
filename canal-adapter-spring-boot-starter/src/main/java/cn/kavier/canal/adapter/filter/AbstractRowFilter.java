package cn.kavier.canal.adapter.filter;

import cn.kavier.canal.adapter.exception.CanalAdapterRuntimeException;
import cn.kavier.canal.adapter.utils.CanalColumnUtil;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractRowFilter implements Filter {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void filter(CanalEntry.Entry entry) {
        CanalEntry.RowChange rowChange;
        try {
            rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            throw new CanalAdapterRuntimeException("ERROR ## parser of StoreValue has an error , data:" + entry, e);
        }

        CanalEntry.EventType eventType = rowChange.getEventType();
        CanalEntry.Header header = entry.getHeader();
        log.debug("binlog[{}:{}] , name[{},{}] , eventType : {}", header.getLogfileName(), header.getLogfileOffset(), header.getSchemaName(), header.getTableName(), eventType);

        // todo 多数据进行插入时，考虑实现批量插入
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            if (eventType == CanalEntry.EventType.DELETE) {
                processDelete(rowData.getBeforeColumnsList());
            } else if (eventType == CanalEntry.EventType.INSERT) {
                processInsert(rowData.getAfterColumnsList());
            } else {
                processUpdate(rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
            }
        }
    }

    public void processInsert(List<CanalEntry.Column> columns) {
        log.debug("insert, data : {}", CanalColumnUtil.toColumnMap(columns));
    }

    public void processDelete(List<CanalEntry.Column> columns) {
        log.debug("delete, data : {}", CanalColumnUtil.toColumnMap(columns));
    }

    public void processUpdate(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        log.debug("update - before, data : {}", CanalColumnUtil.toColumnMap(beforeColumns));
        log.debug("update - after,  data : {}", CanalColumnUtil.toColumnMap(afterColumns, true));
    }
}
