package cn.kavier.canal.adapter.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

/**
 * 数据处理过程
 * data processing
 *
 * @author joey 2022/07/09
 */
public interface Filter {

    void beforeProcess(List<CanalEntry.Entry> entryList);

    void process();

    void afterProcess();

}
