package com.lumem.hgest.controllers;


import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.security.AuthenticationService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class homeController {

    private UserModelAndVeiw userModelAndVeiw;
    private AuthenticationService authenticationService;

    public homeController(UserModelAndVeiw userModelAndVeiw,AuthenticationService authenticationService) {
        this.userModelAndVeiw = userModelAndVeiw;
        this.authenticationService = authenticationService;
    }

    @RequestMapping("/")
    public String nothing(){
        return "redirect:home";
    }

    @RequestMapping("/home")
    public ModelAndView home(){
        return userModelAndVeiw.ModelAndViewWithUser("home");
    }

}

