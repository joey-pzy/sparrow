package cn.joey.canal.adapter;

import cn.joey.canal.adapter.handler.row.RowHandler;

public interface HandlerMapping {

    void initialize();

    RowHandler getRowHandler(String tableName);

}
