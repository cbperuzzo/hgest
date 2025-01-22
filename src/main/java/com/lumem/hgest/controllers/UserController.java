package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOLogin;
import com.lumem.hgest.security.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private AuthenticationService authenticationService;

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @RequestMapping("/confirm/logout")
    public ModelAndView logout(){

        return new ModelAndView("confirmlogout");
    }

    @RequestMapping("/login")
    public ModelAndView login(){
        ModelAndView mv = new ModelAndView("login");
        mv.addObject("dtologin",new DTOLogin());
        return mv;
    }

    @RequestMapping("/register")
    public ModelAndView register(){
        return new ModelAndView("register");
    }

    //@RequestMapping(method = RequestMethod.GET, )

}
