package com.lumem.hgest.controllers;

import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.model.Util.JwtUtil;
import com.lumem.hgest.security.HGestUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtils;
    private final HGestUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtils, HGestUserDetailsService userDetailsService) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        StoredUser user = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

class AuthRequest {
    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}

class AuthResponse {
    private String token;
    public AuthResponse(String token) { this.token = token; }
    public String getToken() { return token; }
}
