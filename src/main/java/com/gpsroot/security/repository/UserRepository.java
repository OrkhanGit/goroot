package com.gpsroot.security.repository;

import com.gpsroot.security.enums.AuthProvider;
import com.gpsroot.security.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByProviderIdAndAuthProvider(String providerId, AuthProvider provider);

}