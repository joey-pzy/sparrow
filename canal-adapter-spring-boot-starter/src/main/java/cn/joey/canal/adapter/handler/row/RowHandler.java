package cn.joey.canal.adapter.handler.row;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * @author joey 2022/07/09
 */
public interface RowHandler {

    void insert(List<CanalEntry.RowData> rowData);

    void update(List<CanalEntry.RowData> rowData);

    void delete(List<CanalEntry.RowData> rowData);

}
