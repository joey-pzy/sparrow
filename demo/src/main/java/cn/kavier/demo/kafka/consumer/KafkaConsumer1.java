package cn.kavier.demo.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer1 {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    //@KafkaListener(topics = "topic0723", groupId = "group1")
    public void lis(ConsumerRecord<String, String> record) {
        logger.info("消费者获取到消息={}", record);
    }
}
