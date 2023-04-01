package cn.joey.canal.adapter.filters;

import cn.joey.canal.adapter.annotation.CanalAdapter;
import cn.joey.canal.adapter.handler.row.RowHandler;
import cn.joey.canal.adapter.template.ESTemplate;
import cn.joey.canal.adapter.utils.CanalColumnUtil;
import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@CanalAdapter(tableName = "tb_canal_animal")
public class AnimalHandler implements RowHandler {

    private static final Logger log = LoggerFactory.getLogger(AnimalHandler.class);

    @Resource
    protected ESTemplate esTemplate;

    @Override
    public void insert(List<CanalEntry.RowData> rowData) {
        for (CanalEntry.RowData rowDatum : rowData) {
            processInsert(rowDatum.getAfterColumnsList());
        }
    }

    @Override
    public void update(List<CanalEntry.RowData> rowData) {
        for (CanalEntry.RowData rowDatum : rowData) {
            processUpdate(rowDatum.getBeforeColumnsList(), rowDatum.getAfterColumnsList());
        }
    }

    @Override
    public void delete(List<CanalEntry.RowData> rowData) {
        log.info("AnimalFilter delete");
        for (CanalEntry.RowData rowDatum : rowData) {
            String id = rowDatum.getAfterColumnsList().stream().filter(CanalEntry.Column::getIsKey).findFirst().get().getValue();
            log.info("delete {}", id);
        }
    }


    public void processInsert(List<CanalEntry.Column> columns) {
        Map<String, Object> stringObjectMap = CanalColumnUtil.toColumnMap(columns);
        log.debug("insert, data : {}", stringObjectMap);
        String keyColumnValue = columns.stream().filter(CanalEntry.Column::getIsKey).findFirst().get().getValue();
        esTemplate.insert(keyColumnValue, JsonUtils.marshalToString(stringObjectMap), "index_canal_animal");
    }

    public void processUpdate(List<CanalEntry.Column> beforeColumns, List<CanalEntry.Column> afterColumns) {
        log.debug("父类update - before, data : {}", CanalColumnUtil.toColumnMap(beforeColumns));
        log.debug("父类update - after,  data : {}", CanalColumnUtil.toColumnMap(afterColumns, true));
        String keyColumnValue = afterColumns.stream().filter(CanalEntry.Column::getIsKey).findFirst().get().getValue();
        esTemplate.update(keyColumnValue, CanalColumnUtil.toColumnMap(afterColumns, true), "index_canal_animal");

    }

}
