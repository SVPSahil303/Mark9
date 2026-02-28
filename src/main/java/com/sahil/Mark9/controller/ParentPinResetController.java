package com.sahil.Mark9.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sahil.Mark9.model.Parent;
import com.sahil.Mark9.repository.ParentRepository;
import com.sahil.Mark9.service.EmailService;
import com.sahil.Mark9.service.OtpService;
import com.sahil.Mark9.service.ParentService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/parent/pin/reset")
public class ParentPinResetController {

    private final OtpService otpService;
    private final EmailService emailService;
    private final ParentRepository parentRepo;
    private final ParentService parentService;

    public ParentPinResetController(
            OtpService otpService,
            EmailService emailService,
            ParentRepository parentRepo,
            ParentService parentService) {
        this.otpService = otpService;
        this.emailService = emailService;
        this.parentRepo = parentRepo;
        this.parentService = parentService;
    }

    /* Step 1: Email input */
    @GetMapping
    public String forgotPinPage() {
        return "parent-pin-forgot";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {

        Parent parent = parentRepo.findByEmail(email).orElse(null);
        if (parent == null) {
            model.addAttribute("error", "Email not registered");
            return "parent-pin-forgot";
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtp(email, otp);

        model.addAttribute("email", email);
        return "parent-pin-otp";
    }

    /* Step 2: OTP verify */
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            Model model) {

        if (!otpService.verifyOtp(email, otp)) {
            model.addAttribute("error", true);
            model.addAttribute("email", email);
            return "parent-pin-otp";
        }

        model.addAttribute("email", email);
        return "parent-pin-reset";
    }

    /* Step 3: Set new PIN */
    @PostMapping("/set-new-pin")
    public String setNewPin(
            @RequestParam String email,
            @RequestParam String pin,
            HttpSession session) {

        Parent parent = parentRepo.findByEmail(email).orElseThrow();
        parent.setPinHash(parentService.encodePin(pin));
        parentRepo.save(parent);

        // force re-verify with new pin
        session.setAttribute("PARENT_PIN_VERIFIED", false);

        return "redirect:/parent/pin?resetSuccess";
    }
}
