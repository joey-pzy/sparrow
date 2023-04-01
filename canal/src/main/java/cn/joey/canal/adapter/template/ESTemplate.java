package cn.joey.canal.adapter.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * es 操作接口
 * todo 针对更新到不同索引的情况，需要考虑实现es多个索引变更的事务
 */
@Component
public class ESTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ESTemplate.class);

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;

    public void insert(String id, String source, String index) {
        List<IndexQuery> list = new ArrayList<>(1);
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(id);
        indexQuery.setSource(source);
        list.add(indexQuery);

        List<IndexedObjectInformation> results = elasticsearchTemplate.bulkIndex(list, IndexCoordinates.of(index));
        logger.info("es插入结果{}", results);
    }


    public void update(String id, Map<String, Object> source, String index) {
        Document document = Document.from(source);
        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocAsUpsert(true).withDocument(document).build();
        UpdateResponse updateResponse = elasticsearchTemplate.update(updateQuery, IndexCoordinates.of(index));
        logger.info("es更新结果{}", updateResponse.getResult());
        // todo 由于canal server 可以保证有序性，mq消费也可以保证有序性，所以 adapter 消费数据也要保证行数据的修改有序性
        // todo 这里通过es提供的外部版本号 比如使用数据的 updateTime 字段，实现 application 消费数据的有序性
    }
}
