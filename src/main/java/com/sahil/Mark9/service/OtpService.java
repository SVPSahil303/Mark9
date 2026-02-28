package com.sahil.Mark9.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.sahil.Mark9.model.ParentOtp;
import com.sahil.Mark9.repository.OtpRepository;

@Service
public class OtpService {

    private final OtpRepository otpRepo;

    public OtpService(OtpRepository otpRepo) {
        this.otpRepo = otpRepo;
    }

    public String generateOtp(String email) {
        otpRepo.deleteByEmail(email);

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        ParentOtp entity = new ParentOtp();
        entity.setEmail(email);
        entity.setOtp(otp);
        entity.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepo.save(entity);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<ParentOtp> record =
                otpRepo.findByEmailAndOtp(email, otp);

        if (record.isEmpty()) return false;

        if (record.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            otpRepo.delete(record.get());
            return false;
        }

        otpRepo.delete(record.get());
        return true;
    }
}
