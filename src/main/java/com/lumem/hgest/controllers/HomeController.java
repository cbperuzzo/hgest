package com.lumem.hgest.controllers;


import com.lumem.hgest.model.DTO.DTOShiftID;
import com.lumem.hgest.model.DTO.DTOUnclosedShift;
import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.Util.Msg;
import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.repository.ShiftRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private ShiftRepository shiftRepository;
    private UserModelAndVeiw userModelAndVeiw;
    private AuthenticationService authenticationService;
    private StoredUserRepository storedUserRepository;

    public HomeController(ShiftRepository shiftRepository, UserModelAndVeiw userModelAndVeiw,
                          AuthenticationService authenticationService, StoredUserRepository storedUserRepository) {
        this.shiftRepository = shiftRepository;
        this.userModelAndVeiw = userModelAndVeiw;
        this.authenticationService = authenticationService;
        this.storedUserRepository = storedUserRepository;
    }


    @RequestMapping({"/home","/"})
    public ModelAndView home(@ModelAttribute("msg") Msg msg){
        ModelAndView mv = userModelAndVeiw.ModelAndViewWithUser("homewithshifts");
        mv.addObject("msg",msg);
        Shift unclosedShift = shiftRepository
                .findByStoredUserAndClosed(storedUserRepository.getReferenceById(authenticationService.getCurrentUserId())
                ,false);
        if (unclosedShift != null){
            mv.addObject("DTOUnclosed", DTOUnclosedShift.createUnclosedShift(unclosedShift));
            mv.addObject("DTOShiftId",new DTOShiftID(unclosedShift.getId()));
        }
        return mv;
    }

}

