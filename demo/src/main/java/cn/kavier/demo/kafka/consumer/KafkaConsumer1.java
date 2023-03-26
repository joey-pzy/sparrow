package cn.kavier.demo.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer1 {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    //@KafkaListener(topics = {"kafka-test-topic"})
    public void get(String message) {
        logger.info("消费者获取到消息={}", message);
    }
}
