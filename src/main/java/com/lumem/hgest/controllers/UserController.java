package com.lumem.hgest.controllers;

import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;

public class UserController {

    public ResponseEntity<?> create(){
        //new user (name password and role)
        // supervisor -> workers
        // admin -> workers, supervisors
        // dev -> all
        return null;
        //TODO
    }

    public ResponseEntity<?> update(){
        //update user (name password and role)
        // supervisor -> workers
        // admin -> workers, supervisors
        // dev -> all
        return null;
        //TODO
    }

    public ResponseEntity<?> disable(){
        //mark as disabled
        //remove all refresh tokens
        return null;
        //TODO
    }

    // get list of user by name LIKE
    // get user by ID

    public record UpdateRequest( long id, @Nullable String name, @Nullable String password) {}
    public record DisableRequest( long id ) {}
    public record CreateRequest( String name, String password) {}
}
