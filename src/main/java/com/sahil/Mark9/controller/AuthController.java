package com.sahil.Mark9.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sahil.Mark9.dto.ParentRegisterDto;
import com.sahil.Mark9.model.Child;
import com.sahil.Mark9.model.LearningType;
import com.sahil.Mark9.model.RewardVideo;
import com.sahil.Mark9.repository.ChildRepository;
import com.sahil.Mark9.repository.RewardVideoRepository;
import com.sahil.Mark9.service.ParentService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RewardVideoRepository rewardVideoRepository;

    // ================= REGISTER =================

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("parent", new ParentRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute("parent") ParentRegisterDto dto,
                                     Model model) {
        try {
            parentService.registerParent(dto);
            return "redirect:/login?registered";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    // ================= PARENT LOGIN =================

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ================= CHILD LOGIN =================

    @GetMapping("/child/login")
    public String showChildLogin() {
        return "child-login";
    }

    @PostMapping("/child/login")
    public String processChildLogin(@RequestParam String username,
                                    @RequestParam String password,
                                    HttpSession session,
                                    Model model) {

        Optional<Child> optionalChild = childRepository.findByUsername(username);

        if (optionalChild.isEmpty()) {
            model.addAttribute("error", "No child found with that username");
            return "child-login";
        }

        Child child = optionalChild.get();

        if (!passwordEncoder.matches(password, child.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            return "child-login";
        }

        session.setAttribute("LOGGED_IN_CHILD_ID", child.getId());

        return "redirect:/child/mode";
    }

    @GetMapping("/child/mode")
    public String showModePage(HttpSession session, Model model) {
        Long childId = (Long) session.getAttribute("LOGGED_IN_CHILD_ID");
        if (childId == null) return "redirect:/child/login";

        Child child = childRepository.findById(childId).orElseThrow();
        model.addAttribute("child", child);

        return "child-mode";
    }


    @GetMapping("/child/logout")
    public String childLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/child/login?logout";
    }

    // ================= LEARNING PAGE =================

    @GetMapping("/child/learn")
    public String showLearnPage(HttpSession session,
                                @RequestParam(defaultValue = "NUMBER") LearningType type,
                                Model model) {

        Long childId = (Long) session.getAttribute("LOGGED_IN_CHILD_ID");

        if (childId == null) {
            return "redirect:/child/login";
        }

        Child child = childRepository.findById(childId).orElseThrow();

        model.addAttribute("child", child);
        model.addAttribute("learningType", type);

        // 🎥 Reward videos filtered by type
        if (child.getParent() != null) {
            List<RewardVideo> rewardVideos =
                    rewardVideoRepository.findByParentAndLearningType(
                            child.getParent(),
                            type
                    );
            model.addAttribute("rewardVideos", rewardVideos);
        }

        // 🔥 DERIVE current symbol (DO NOT STORE)
        String currentSymbol;

        if (type == LearningType.NUMBER) {

            Integer last = child.getLastDigitPracticed();
            currentSymbol = (last == null) ? "0" : String.valueOf(last + 1);

        } else {

            String last = child.getLastAlphabetPracticed();
            currentSymbol = (last == null)
                    ? "A"
                    : String.valueOf((char) (last.charAt(0) + 1));
        }

        model.addAttribute("currentSymbol", currentSymbol);

        return "canvas";
    }
}
