package com.lumem.hgest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import org.springframework.context.annotation.Primary;

@Entity
public class RegisterKey {

    @GeneratedValue
    @Id
    private Long id;
    private String value;
    private boolean valid;
}
