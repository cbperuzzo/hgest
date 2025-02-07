package com.lumem.hgest.controllers;


import com.lumem.hgest.model.Util.Msg;
import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.security.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private UserModelAndVeiw userModelAndVeiw;
    private AuthenticationService authenticationService;

    public HomeController(UserModelAndVeiw userModelAndVeiw, AuthenticationService authenticationService) {
        this.userModelAndVeiw = userModelAndVeiw;
        this.authenticationService = authenticationService;
    }


    @RequestMapping({"/home","/"})
    public ModelAndView home(@ModelAttribute("msg") Msg msg){
        ModelAndView mv = userModelAndVeiw.ModelAndViewWithUser("home");
        mv.addObject("msg",msg);
        return mv;
    }

}

