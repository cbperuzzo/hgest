package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Util.SecurityUser;
import com.lumem.hgest.model.Util.JwtUtil;
import com.lumem.hgest.model.Util.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtils, RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }


    @Deprecated
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            SecurityUser user = (SecurityUser) auth.getPrincipal();

            String token = jwtUtils.generateAccessToken(user);
            return ResponseEntity.ok(new AccessToken(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


    }

    @PostMapping("/login/remember")
    public ResponseEntity<?> loginRemember(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            SecurityUser user = (SecurityUser) auth.getPrincipal();

            String refreshToken = refreshTokenService.issue(user);
            String accessToken =  jwtUtils.generateAccessToken(user);

            return ResponseEntity.ok(new TokenPair(refreshToken,accessToken));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> loginRefresh(@RequestBody RefreshRequest request ){

        try {
            String newRefreshToken = refreshTokenService.refresh(request.refreshToken);
            String newAccessToken = jwtUtils.generateAccessToken(refreshTokenService.extractUser(request.refreshToken));
            return ResponseEntity.ok(new TokenPair(newRefreshToken,newAccessToken));
        }
        catch (Exception ignored){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record LoginRequest (String username, String password){ }

    public record AccessToken(String token) { }

    public record TokenPair(String refresh, String access) { }

    public record RefreshRequest(String refreshToken){ }

}

