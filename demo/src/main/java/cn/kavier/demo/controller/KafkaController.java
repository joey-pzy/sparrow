package cn.kavier.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class KafkaController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/kafka/push/{topic}")
    public void kafkaPush(@PathVariable String topic, @RequestParam String msg) {
        kafkaTemplate.send(topic, msg);
        logger.info("生产者发出消息topic={},msg={}", topic, msg);
    }

}
