package cn.kavier.canal.adapter.loader;

import cn.kavier.canal.adapter.filter.AbstractFilter;
import cn.kavier.canal.adapter.filter.CommonFilter;
import org.springframework.stereotype.Component;

@Component
public class FilterLoader {

    public AbstractFilter getFilter(String name) {
        // todo 实现获取不同 filter
        return getDefaultFilter();
    }

    public AbstractFilter getDefaultFilter() {
        // todo 首先从ioc中拿，没有再实例化一个，然后放入到ioc中，保持单例

        return new CommonFilter();
    }
}
