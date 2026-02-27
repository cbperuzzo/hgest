package com.lumem.hgest.controllers;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("shift")
@Transactional
public class ShiftController {

    //get by id
    //get all* by user in a date range (optional, default is "last month")
    //get total time by user in range a range (maybe bundle this to the above one ?, but maintain this standalone also)
    //get all by service in a date range (optional, default is "last month")

}
