package com.sahil.Mark9.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sahil.Mark9.model.ParentOtp;

@Repository
public interface OtpRepository extends JpaRepository<ParentOtp, Long> {

    Optional<ParentOtp> findByEmailAndOtp(String email, String otp);

    void deleteByEmail(String email);
}
