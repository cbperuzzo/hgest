package com.lumem.hgest.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class RegisterKey{
    @Id
    @GeneratedValue
    private long id;

    private String value;
}
