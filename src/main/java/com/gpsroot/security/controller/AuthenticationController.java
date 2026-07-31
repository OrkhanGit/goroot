package com.gpsroot.security.controller;


import com.gpsroot.security.dto.*;
import com.gpsroot.security.service.AuthenticationService;
import com.gpsroot.security.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/save")
    public ResponseEntity<String> save (@RequestBody UserDto userDto) {
        return authenticationService.save(userDto);
    }

    @PostMapping("/auth")
    public ResponseEntity<UserResponse> auth (@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authenticationService.auth(userRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse> refresh (@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authenticationService.refresh(refreshRequest));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return passwordResetService.forgotPassword(request);
    }

    @PostMapping("/password/verify-code")
    public ResponseEntity<VerifyCodeResponse> verifyCode(@RequestBody VerifyCodeRequest request) {
        return ResponseEntity.ok(passwordResetService.verifyCode(request));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return passwordResetService.resetPassword(request);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(@RequestBody RefreshRequest refreshRequest) {
//        authenticationService.logout(refreshRequest);
//        return ResponseEntity.noContent().build();
//    }

}