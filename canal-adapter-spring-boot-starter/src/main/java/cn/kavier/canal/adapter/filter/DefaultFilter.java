package cn.kavier.canal.adapter.filter;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.stereotype.Component;

@Component
public class DefaultFilter extends AbstractFilter {

    @Override
    public void beforeProcess(CanalEntry.Entry entry) {
        super.beforeProcess(entry);
    }

    @Override
    public void process() {
        super.process();
    }

    @Override
    public void processInsert() {
        super.processInsert();
    }

    @Override
    public void processDelete() {
        super.processDelete();
    }

    @Override
    public void processUpdate() {
        super.processUpdate();
    }

    @Override
    public void afterProcess() {
        super.afterProcess();
    }
}
