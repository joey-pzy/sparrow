package cn.joey.canal.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CanalApplication {

    private static final Logger log = LoggerFactory.getLogger(CanalApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
        log.info("=================canal项目 启动完成==================");
    }

}
