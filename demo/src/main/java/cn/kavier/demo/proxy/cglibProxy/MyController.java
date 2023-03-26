package cn.kavier.demo.proxy.cglibProxy;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

public class MyController {

    //@Transactional
    public void print(String name) {
        System.out.println("名称输出："+name);
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> getStudent(Integer id) {
        Map<String, String> result = new HashMap<>();
        result.put("name", "joey");
        result.put("age", "25");
        return result;
    }

}
