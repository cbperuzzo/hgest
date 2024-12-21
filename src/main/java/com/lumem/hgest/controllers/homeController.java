package com.lumem.hgest.controllers;

import com.lumem.hgest.security.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class homeController {

    AuthenticationService authenticationService;

    public homeController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping("/home")
    @ResponseBody
    public String home(){
        return "oi oi oi";
    }

    @RequestMapping("/hometest")
    @ResponseBody
    public String homeTest(){
        return authenticationService.getCurrentUserName();
    }

}
