package cn.joey.canal.adapter.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * @author Joey
 */
public interface EntryHandler {

    void handle(List<CanalEntry.Entry> entries);

}
