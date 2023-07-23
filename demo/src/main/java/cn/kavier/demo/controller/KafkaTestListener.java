package cn.kavier.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaTestListener {

    Logger logger = LoggerFactory.getLogger(KafkaTestListener.class);

    @KafkaListener(topics = "topic072301", groupId = "test-group", id = "test-group")
    public void doLis(String msg) {
        logger.info(msg);
    }
}
