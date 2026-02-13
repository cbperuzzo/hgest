package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Util.JwtUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        return null;
    }

    @RequestMapping("get/application/name")
    @ResponseBody
    public String getApplicationName(){
        return "HGest";
    }

}
