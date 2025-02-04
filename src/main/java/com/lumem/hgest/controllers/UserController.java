package com.lumem.hgest.controllers;

import com.lumem.hgest.model.DTO.DTOLogin;
import com.lumem.hgest.model.DTO.DTORegister;
import com.lumem.hgest.model.Role.RoleEnum;
import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.model.Util.Msg;
import com.lumem.hgest.model.Util.StoredUserCreator;
import com.lumem.hgest.repository.RegisterKeyRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private StoredUserRepository storedUserRepository;
    private RegisterKeyRepository registerKeyRepository;
    private StoredUserCreator storedUserCreator;

    public UserController(StoredUserRepository storedUserRepository,
                          RegisterKeyRepository registerKeyRepository,StoredUserCreator storedUserCreator) {
        this.storedUserRepository = storedUserRepository;
        this.registerKeyRepository = registerKeyRepository;
        this.storedUserCreator = storedUserCreator;
    }

    @RequestMapping("/confirm/logout")
    public ModelAndView logout(){

        return new ModelAndView("confirmlogout");
    }

    @RequestMapping("/login")
    public ModelAndView login(@ModelAttribute("msg") Msg msg, @RequestParam(value = "error",required = false) String error){
        ModelAndView mv = new ModelAndView("login");
        String m;
        if(error != null){
            msg.setBody("wrong username or password");
            msg.setCode("fail");
        }
        mv.addObject("msg",msg);

        mv.addObject("dtologin",new DTOLogin());

        return mv;
    }

    @RequestMapping("/register")
    public ModelAndView register(@ModelAttribute("msg") Msg msg){
        ModelAndView mv = new ModelAndView("register");
        mv.addObject("msg",msg);
        mv.addObject("dtoregister",new DTORegister());
        return mv;
    }


    @Transactional
    @RequestMapping(value = "/register/processing",method = RequestMethod.POST)
    public String registerProcessing(@ModelAttribute("dtoregister") DTORegister dtoRegister, RedirectAttributes redirectAttributes){
        Msg msg = new Msg("sucssefully registerd","success");
        boolean approved = true;
        if(storedUserRepository.existsByUserName(dtoRegister.getUsername()) || dtoRegister.getUsername().isBlank() ){
            msg.setBody("User name already exists or is blank");
            msg.setCode("fail");
            approved = false;
        }
        if(!dtoRegister.doPasswordsMatch() || dtoRegister.getPassword().isBlank()){
            msg.setBody("Password confirmation does not match actual password or password is blank");
            msg.setCode("fail");
            approved = false;
        }
        if(!registerKeyRepository.existsByValueAndValid(dtoRegister.getKey(),true)){
            msg.setBody("Register key is either invalid or non existent");
            msg.setCode("fail");
            approved = false;
        }

        redirectAttributes.addFlashAttribute("msg",msg);

        if (approved){

            StoredUser storedUser = storedUserCreator.createUser(dtoRegister.getPassword(),dtoRegister.getUsername()
                    ,RoleEnum.WORKER); //default for newly created users
            storedUserRepository.save(storedUser);
            return "redirect:/login";
        }
        return "redirect:/register";
    }

}
