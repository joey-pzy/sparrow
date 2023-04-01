package cn.joey.canal.adapter;

import cn.joey.canal.adapter.annotation.CanalAdapter;
import cn.joey.canal.adapter.handler.row.RowHandler;
import cn.joey.canal.adapter.properties.CanalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class RowHandlerMapping implements HandlerMapping {

    private static final Logger logger = LoggerFactory.getLogger(RowHandlerMapping.class);

    private final Map<String, RowHandler> handlerMap = new HashMap<>();

    private final ApplicationContext applicationContext;

    private final CanalProperties canalProperties;

    private static final String COMMA;

    static {
        COMMA = ",";
    }

    public RowHandlerMapping(ApplicationContext applicationContext, CanalProperties canalProperties) {
        this.applicationContext = applicationContext;
        this.canalProperties = canalProperties;
    }

    @Override
    public void initialize() {
        logger.info("initialize canal HandlerMapping ...");
        long startMs = System.currentTimeMillis();
        String[] beanNamesForAnnotation = applicationContext.getBeanNamesForAnnotation(CanalAdapter.class);
        for (String beanName : beanNamesForAnnotation) {
            CanalAdapter canalAdapter = applicationContext.findAnnotationOnBean(beanName, CanalAdapter.class);
            if (canalAdapter != null) {
                handlerMap.put(canalAdapter.tableName(), applicationContext.getBean(beanName, RowHandler.class));
            }
        }
        logger.info("Finished HandlerMapping initializing in {}ms.", System.currentTimeMillis() - startMs);
    }

    @Override
    public RowHandler getRowHandler(String tableName) {
        return handlerMap.get(tableName);
    }

}
