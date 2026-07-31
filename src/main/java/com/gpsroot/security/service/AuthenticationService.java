package com.gpsroot.security.service;

import com.gpsroot.security.dto.RefreshRequest;
import com.gpsroot.security.dto.UserDto;
import com.gpsroot.security.dto.UserRequest;
import com.gpsroot.security.dto.UserResponse;
import com.gpsroot.security.enums.Roles;
import com.gpsroot.security.model.RefreshToken;
import com.gpsroot.security.model.Users;
import com.gpsroot.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<String> save(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Users user = Users.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .gender(userDto.getGender())
                .role(Roles.USER)
                .build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");

    }

    public UserResponse auth(UserRequest userRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        Users user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow();

        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return UserResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public UserResponse refresh(RefreshRequest refreshRequest) {

        RefreshToken storedToken = refreshTokenService.verifyRefreshToken(refreshRequest.getRefreshToken());
        Users user = storedToken.getUser();

        String newAccessToken = jwtService.generateToken(user);

        return UserResponse.builder()
                .token(newAccessToken)
                .refreshToken(storedToken.getToken())
                .build();
    }

//    public void logout(RefreshRequest refreshRequest) {
//        refreshTokenService.revokeToken(refreshRequest.getRefreshToken());
//    }
}