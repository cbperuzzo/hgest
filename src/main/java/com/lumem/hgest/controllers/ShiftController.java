package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOOpenShift;
import com.lumem.hgest.model.DTO.DTOShiftID;
import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.Util.Msg;
import com.lumem.hgest.model.Util.TimeDifference;
import com.lumem.hgest.model.Util.UserModelAndVeiw;
import com.lumem.hgest.model.Util.err.ValidationException;
import com.lumem.hgest.repository.ShiftRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class ShiftController {

    private ShiftRepository shiftRepository;
    private StoredUserRepository storedUserRepository;
    private UserModelAndVeiw userModelAndVeiw;
    private AuthenticationService authenticationService;

    public ShiftController(ShiftRepository shiftRepository, StoredUserRepository storedUserRepository,
                           UserModelAndVeiw userModelAndVeiw, AuthenticationService authenticationService) {
        this.shiftRepository = shiftRepository;
        this.storedUserRepository = storedUserRepository;
        this.userModelAndVeiw = userModelAndVeiw;
        this.authenticationService = authenticationService;
    }

    @RequestMapping("/new/shift")
    @PreAuthorize("hasAuthority('WORKER')")
    public ModelAndView newShiftView(@ModelAttribute("msg") Msg msg){
        ModelAndView mv = userModelAndVeiw.ModelAndViewWithUser("newshift");
        mv.addObject("msg",msg);
        mv.addObject("DTOOpenShift", DTOOpenShift.placeholderEntry());
        return mv;
    }

    @Transactional
    @RequestMapping(value = "/new/shift/processing",method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('WORKER')")
    public String newShiftProcessing(@ModelAttribute("DTOOpenShift") DTOOpenShift dtoOpenShift, RedirectAttributes redirectAttributes){
        Msg msg = new Msg("new shift opened successfully","success");
        String redirect = "redirect:/home";

        try {
            if (shiftRepository.existsByOs(dtoOpenShift.getOs())){
                throw new ValidationException("duplicate os");
            }

            if (dtoOpenShift.getSegment().isBlank() || dtoOpenShift.getOs().isBlank()){
                throw new ValidationException("empty fields");
            }

            if (shiftRepository.existsByStoredUserAndClosed(storedUserRepository.getReferenceById(authenticationService.getCurrentUserId()),false)){
                throw new ValidationException("you have an unclosed shift");
            }

            Shift nShift = new Shift(
                storedUserRepository.getReferenceById(authenticationService.getCurrentUserId()),
                dtoOpenShift.getOs(),
                dtoOpenShift.getSegment(),
                LocalTime.parse(dtoOpenShift.getTime()),
                LocalDate.parse(dtoOpenShift.getDate())
        );
            shiftRepository.save(nShift);

        }catch (ValidationException e){
            msg.setBody(e.getMessage());
            msg.setCode("fail");
            redirect = "redirect:/new/shift";
        }
        catch (Exception e){
            msg.setBody("something went wrong");
            msg.setCode("fail");
            redirect = "redirect:/new/shift";
        }

        redirectAttributes.addFlashAttribute("msg",msg);

        return redirect;

    }
    @PreAuthorize("hasAuthority('WORKER')")
    @RequestMapping(value = "/close/shift/processing",method = RequestMethod.POST)
    public String closeShift(@ModelAttribute("DTOShiftId") DTOShiftID dtoShiftID, RedirectAttributes redirectAttributes){
        Msg msg = new Msg("shift closed successfully","success");
        try{
            Shift shift = shiftRepository.findById(dtoShiftID.getId()).orElseThrow();
            if (shift.getStoredUser().getId()!=authenticationService.getCurrentUserId()){
                throw new ValidationException("shift user and current user don't match");
            }
            shift.setClosed(true);
            shift.setCloseDate(LocalDate.now());
            shift.setCloseTime(LocalTime.now());
            long dif = TimeDifference.calc(shift.getOpenTime(),shift.getOpenDate(),shift.getCloseTime(),shift.getCloseDate());
            shift.setTotalMinutes(dif);
            shiftRepository.save(shift);
        }
        catch (ValidationException e){
            msg.setBody(e.getMessage());
            msg.setCode("fail");
        }
        catch (Exception e){
            msg.setBody("something went wrong");
            msg.setCode("fail");
            System.out.println(e.getMessage());
        }
        redirectAttributes.addFlashAttribute("msg",msg);
        return "redirect:/home";
    }
}

