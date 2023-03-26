package cn.joey.canal.adapter.filters;

import cn.joey.canal.adapter.filter.AbstractRowFilter;
import cn.joey.canal.adapter.template.ESTemplate;
import cn.joey.canal.adapter.utils.CanalColumnUtil;
import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component(value = "af")
public class AnimalFilter extends AbstractRowFilter {

    @Resource
    protected ESTemplate esTemplate;

    @Autowired
    public AnimalFilter(ESTemplate esTemplate) {
        this.esTemplate = esTemplate;
    }

    @Override
    public void processInsert(List<CanalEntry.Column> columns, String index) {
        Map<String, Object> stringObjectMap = CanalColumnUtil.toColumnMap(columns);
        log.debug("insert, data : {}", stringObjectMap);
        String keyColumnValue = columns.stream().filter(CanalEntry.Column::getIsKey).findFirst().get().getValue();
        esTemplate.insert(keyColumnValue, JsonUtils.marshalToString(stringObjectMap), "index_canal_animal");
    }

    @Override
    public void processDelete(List<CanalEntry.Column> columns) {
        log.info("AnimalFilter delete");
        super.processDelete(columns);
    }

    @Override
    public void processUpdate(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        log.debug("父类update - before, data : {}", CanalColumnUtil.toColumnMap(beforeColumns));
        log.debug("父类update - after,  data : {}", CanalColumnUtil.toColumnMap(afterColumns, true));
        String keyColumnValue = afterColumns.stream().filter(CanalEntry.Column::getIsKey).findFirst().get().getValue();
        esTemplate.update(keyColumnValue, CanalColumnUtil.toColumnMap(afterColumns, true), "index_canal_animal");

    }
}
