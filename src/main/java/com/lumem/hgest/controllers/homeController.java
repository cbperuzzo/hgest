package com.lumem.hgest.controllers;


import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.security.AuthenticationService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @RequestMapping("/admin")
    @ResponseBody
    @PreAuthorize("hasAuthority('ADMIN')")
    public String admin(){
        return "super secret admin only text";
    }

    @RequestMapping({"/home","/"})
    public ModelAndView home(){
        return userModelAndVeiw.ModelAndViewWithUser("home");
    }

}

