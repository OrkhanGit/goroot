package com.gpsroot.security.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.gpsroot.security.enums.AuthProvider;
import com.gpsroot.security.enums.Roles;
import com.gpsroot.security.model.Users;
import com.gpsroot.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {

    private final UserRepository usersRepository;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthService(UserRepository usersRepository,
                             @Value("${google.client.id}") String googleClientId) {
        this.usersRepository = usersRepository;
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }

    public Users verifyAndGetOrCreateUser(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Yanlış Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            return usersRepository.findByProviderIdAndAuthProvider(googleId, AuthProvider.GOOGLE)
                    .orElseGet(() -> usersRepository.findByEmail(email)
                            .orElseGet(() -> createNewGoogleUser(googleId, email, name)));

        } catch (Exception e) {
            throw new RuntimeException("Google token doğrulanmadı: " + e.getMessage());
        }
    }

    private Users createNewGoogleUser(String googleId, String email, String name) {
        String username = (name != null && !name.isBlank())
                ? name
                : email.split("@")[0];

        Users user = Users.builder()
                .email(email)
                .username(username)
                .providerId(googleId)
                .authProvider(AuthProvider.GOOGLE)
                .role(Roles.USER)
                .build();
        return usersRepository.save(user);
    }
}