package com.lumem.hgest.controllers;

import com.lumem.hgest.model.service.Service;
import com.lumem.hgest.model.service.ServiceDTO;
import com.lumem.hgest.repository.ServiceRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> openService(@Valid @RequestBody OpenServiceRequest request){
        Service service = new Service(request.title());
        service.setOpenDate(LocalDate.now());
        service.setOpenTime(LocalTime.now());
        service.setDescription(request.description());
        service.setClosed(false);
        service.setOs(request.os);

        serviceRepository.save(service);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/close/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> closeService(@PathVariable("id") Long id){

        if (!serviceRepository.existsById(id)){
            return ResponseEntity.status(400).body("no such service");
        }

        Service service = serviceRepository.getReferenceById(id);

        if (service.isClosed()){
            return ResponseEntity.status(400).body("already closed");
        }

        service.setClosed(true);
        service.setCloseTime(LocalTime.now());
        service.setCloseDate(LocalDate.now());

        serviceRepository.save(service);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequest request
    ){

        if (!serviceRepository.existsById(id))
            return ResponseEntity.badRequest().body("service not found");

        Service service = serviceRepository.getReferenceById(id);

        if (request.title() != null && !request.title().isEmpty())
            service.setTitle(request.title());

        if (request.description() != null && !request.description.isEmpty())
            service.setDescription(request.description());

        if (request.os() != null)
            service.setOs(request.os());

        serviceRepository.save(service);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public Page<ServiceDTO> getByTitle(
            @RequestParam String title,
            @PageableDefault(size = 5) Pageable pageable
    ){

        return serviceRepository
                .findByTitleContainingIgnoreCase(title, pageable)
                .map(ServiceDTO::new);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> getById(@PathVariable Long id){

        if (!serviceRepository.existsById(id))
            return ResponseEntity.status(400).body("no such service");

        return ResponseEntity.ok(new ServiceDTO(serviceRepository.getReferenceById(id)));

    }

    @GetMapping("/by-os")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public Page<ServiceDTO> getByOs(
            @RequestParam long os,
            @PageableDefault(size = 5) Pageable pageable
    ){

        return serviceRepository
                .findByOs(os, pageable)
                .map(ServiceDTO::new);
    }

    @GetMapping("")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public Page<ServiceDTO> getAll(
            @RequestParam(required = false) Boolean closed,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 5) Pageable pageable
    ){
        if (startDate == null){
            startDate = LocalDate.now().minusMonths(1);
        }
        if (endDate == null){
            endDate = LocalDate.now();
        }

        return serviceRepository
                .findWithFilters(closed, startDate, endDate, pageable)
                .map(ServiceDTO::new);
    }


    public record OpenServiceRequest(@NotNull String title, String description,@NotNull Long os) {}

    public record UpdateServiceRequest(@Nullable String title,@Nullable String description,@Nullable Long os) {}

}
