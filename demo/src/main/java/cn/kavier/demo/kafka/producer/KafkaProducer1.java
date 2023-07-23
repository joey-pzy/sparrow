package cn.kavier.demo.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KafkaProducer1 {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    //@Scheduled(fixedDelay = 3000)
    public void send() {
        kafkaTemplate.send("kafka-test-topic", "1", "{\"name\":\"张三\"}");
        logger.info("生产者发出消息={}", "Joey test kafka!");
    }
}
