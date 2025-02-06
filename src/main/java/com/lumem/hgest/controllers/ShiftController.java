package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOOpenShift;
import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ShiftController {
    private StoredUserRepository storedUserRepository;
    private UserModelAndVeiw userModelAndVeiw;
    private AuthenticationService authenticationService;

    public ShiftController(StoredUserRepository storedUserRepository, UserModelAndVeiw userModelAndVeiw, AuthenticationService authenticationService) {
        this.storedUserRepository = storedUserRepository;
        this.userModelAndVeiw = userModelAndVeiw;
        this.authenticationService = authenticationService;
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
    @ResponseBody
    public String newShiftProcessing(@ModelAttribute("DTOOpenShift") DTOOpenShift dtoOpenShift){
        Shift newShift = new Shift(
                storedUserRepository.getReferenceById(authenticationService.getCurrentUserId()),
                dtoOpenShift.getOs(),
                dtoOpenShift.getSegment(),
                LocalTime.parse(dtoOpenShift.getTime()),
                LocalDate.parse(dtoOpenShift.getDate())
        );

        return newShift.toString() + "<br>"+ dtoOpenShift;

    }
}
