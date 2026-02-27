package com.sahil.Mark9.controller;



import com.sahil.Mark9.model.Parent;
import com.sahil.Mark9.repository.ParentRepository;
import com.sahil.Mark9.service.EmailService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/child")
public class VisibilityController {

    private final EmailService emailService;
    private final ParentRepository parentRepository;
    public VisibilityController(EmailService emailService,ParentRepository parentRepository) {
        this.emailService = emailService;
        this.parentRepository=parentRepository;
    }

    @GetMapping("/alert-parent")
    public ResponseEntity<String> alertParent(Authentication authentication) {
        String email=authentication.getName();
        Parent parent=parentRepository.findByEmail(email).orElseThrow();
        String to = parent.getEmail(); // parent's email
        String subject = "⚠️ Child switched away from the learning tab!";
        String text = "Your child just switched to another browser tab during learning time.";
       

        try {
            emailService.sendSimpleEmail(to, subject, text);
            System.out.println("EmailService: alert sent to " + to);
            return ResponseEntity.ok("alert sent");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("failed to send alert: " + ex.getMessage());
        }
    }


    @GetMapping("/Detect")
    public String detect(){
        return "home";
    }
}

