package cn.kavier.demo.es;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestEsInsert {

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Scheduled(fixedRate = 3000)
    public void insert() {

        List<IndexQuery> list = new ArrayList<>(1);
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setSource("{\"id\":1, \"name\": \"joey\", \"desc\":\"这是备注\"}");
        list.add(indexQuery);

        List<IndexedObjectInformation> results = elasticsearchTemplate.bulkIndex(list, IndexCoordinates.of("index_canal_animal"));
        System.out.println(results);
    }
}
