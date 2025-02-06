package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOOpenShift;
import com.lumem.hgest.model.Util.UserModelAndVeiw;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ShiftController {

    private UserModelAndVeiw userModelAndVeiw;

    public ShiftController(UserModelAndVeiw userModelAndVeiw) {
        this.userModelAndVeiw = userModelAndVeiw;
    }

    @RequestMapping("/new/shift")
    @PreAuthorize("hasAuthority('WORKER')")
    public ModelAndView newShiftView(){
        ModelAndView mv = userModelAndVeiw.ModelAndViewWithUser("newshift");
        mv.addObject("DTOOpenShift", DTOOpenShift.placeholderEntry());
        return mv;
    }

    @Transactional
    @RequestMapping(value = "/new/shift/processing",method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('WORKER')")
    public String newShiftProcessing(@RequestParam("DTOOpenShift") DTOOpenShift os){


    }
}
