package com.sahil.Mark9.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sahil.Mark9.service.EmailService;


@Controller
public class TestController {

    private final EmailService emailService;
    
    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @GetMapping("/")
    public String home(){
        return "canvas_test";
    }

    @GetMapping("/test")
    public String test(){
        return "home";
    }

    @GetMapping("/result")
    public String showResult(Model model) {
        return "canvas"; // reuse canvas.html for result display
    }

    @PostMapping("/send-email")
    public String sendEmail(RedirectAttributes redirectAttributes) {
        try{
            String subject="Demo: Email from SpringBoot";
            String body="This is a demo email sent from Spring Boot when you clicked the button.";
            emailService.sendSimpleEmail("patilsahil350@gmail.com",subject,body);
            redirectAttributes.addFlashAttribute("message", "Email sent successfully to " + "Patilsahil350@gmail.com");
        }catch(Exception ex){
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error","Failed to send email: " + ex.getMessage());
        }
        return "redirect:/test";
    }
    
}
