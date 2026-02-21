package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Service;
import com.lumem.hgest.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/service")
@Transactional
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> openService(@RequestBody OpenServiceRequest request){
        Service service = new Service(request.title());
        service.setOpenDate(LocalDate.now());
        service.setOpenTime(LocalTime.now());
        service.setDescription(request.description());
        service.setClosed(false);
        service.setOs(request.os);

        serviceRepository.save(service);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/close")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> closeService(@RequestBody CloseServiceRequest request){

        serviceRepository.closeService(request.id(),LocalTime.now(),LocalDate.now());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> update(){
        //TODO
        return null;
    }

    //get by id
    //get by title LIKE
    //get by OS
    //get all* by range
    //get all* closed
    //get all* open


    public record OpenServiceRequest(String title, String description, Long os) {}

    public record CloseServiceRequest(long id) {}

}
