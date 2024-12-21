package com.lumem.hgest.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Role{
    @Id
    @GeneratedValue
    private long id;

    private String value;
}
