package cn.joey.canal.adapter.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * 数据处理过程
 * data processing
 *
 * @author joey 2022/07/09
 */
public interface Filter {

    void filter(CanalEntry.Entry entry);

}
