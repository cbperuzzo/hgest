package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOOpenShift;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EntryController {

    @RequestMapping(value = "/new/entry", method = RequestMethod.GET)
    public ModelAndView newEntry(){
        ModelAndView mv = new ModelAndView("newentry");
        mv.addObject("dtoentry", DTOOpenShift.placeholderEntry());
        return mv;
    }



}
