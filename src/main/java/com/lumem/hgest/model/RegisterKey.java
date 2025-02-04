package com.lumem.hgest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class RegisterKey {

    @GeneratedValue
    @Id
    private Long id;
    private String value;
    private boolean valid;
}
