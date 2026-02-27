package com.lumem.hgest.controllers;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shift")
@Transactional
public class ShiftController {

    //get by id
    //get all* by user in range (closed)
    //get total time by user in range (closed)
    //get all* by service

}
