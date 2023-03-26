package cn.joey.canal.adapter.utils;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pzy 2022-07-17
 * @since 1.0.0
 */
public class CanalColumnUtil {

    public static Map<String, Object> toColumnMap(List<CanalEntry.Column> columns) {
        return toColumnMap(columns, false);
    }

    public static Map<String, Object> toColumnMap(List<CanalEntry.Column> columns, boolean onlyUpdated) {
        if (null == columns || columns.isEmpty()) {
            return new HashMap<>(0);
        }
        Map<String, Object> map = new HashMap<>(columns.size());
        for (CanalEntry.Column column : columns) {
            if (onlyUpdated && !column.getUpdated() && !column.getIsKey()) {
                continue;
            }
            map.put(column.getName(), column.getValue());
        }
        return map;
    }

}
