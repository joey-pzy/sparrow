package cn.joey.netty.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flux")
public class MyWebFluxController {

    @GetMapping("/{id}")
    public String getName(@PathVariable("id") Integer id) {
        return "joey";
    }
}
