package com.lumem.hgest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class RegisterKey {
    @Id
    @GeneratedValue
    private Long id;
    private String Value;
    private boolean isValid;
}
