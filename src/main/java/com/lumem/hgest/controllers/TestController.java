package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Util.SecurityUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    public TestController() {
    }

    @RequestMapping("name")
    @ResponseBody
    public String name(){
        return ( (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    @RequestMapping("get/application/name")
    @ResponseBody
    public String getApplicationName(){
        return "HGest";
    }

}
