package com.lumem.hgest.model.Util;

import com.lumem.hgest.security.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class UserModelAndVeiw{

    private AuthenticationService authenticationService;

    public UserModelAndVeiw(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public ModelAndView ModelAndViewWithUser(String s){
        ModelAndView mv = new ModelAndView(s);
        mv.addObject("user",authenticationService.getCurrentUser());
        return mv;
    }
}
