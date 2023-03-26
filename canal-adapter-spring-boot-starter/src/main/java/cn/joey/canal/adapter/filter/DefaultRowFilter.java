package cn.joey.canal.adapter.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultRowFilter extends AbstractRowFilter {

    private String esIndex;

    public DefaultRowFilter() {}

    public DefaultRowFilter(String esIndex) {
        this.esIndex = esIndex;
    }

    @Override
    protected void processInsert(List<CanalEntry.Column> columns, String index) {

    }

    @Override
    protected void processUpdate(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {

    }
}
