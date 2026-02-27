package com.sahil.Mark9.controller;

import com.sahil.Mark9.model.*;
import com.sahil.Mark9.repository.*;
import com.sahil.Mark9.service.EmailService;
import com.sahil.Mark9.service.ParentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/parent")
public class ParentController {

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final ProgressRepository progressRepository;
    private final RewardVideoRepository rewardVideoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    private ParentService parentService;

    public ParentController(ParentRepository parentRepository,
                            ChildRepository childRepository,
                            ProgressRepository progressRepository,
                            RewardVideoRepository rewardVideoRepository,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.parentRepository = parentRepository;
        this.childRepository = childRepository;
        this.progressRepository = progressRepository;
        this.rewardVideoRepository = rewardVideoRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // ================= DASHBOARD =================

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();

        List<Child> children = childRepository.findByParent(parent);
        List<RewardVideo> rewardVideos = rewardVideoRepository.findByParent(parent);

        model.addAttribute("parent", parent);
        model.addAttribute("children", children);
        model.addAttribute("newChild", new Child());
        model.addAttribute("rewardVideos", rewardVideos);
        model.addAttribute("newRewardVideo", new RewardVideo());

        return "parent-dashboard";
    }

    // ================= ADD CHILD =================

    @PostMapping("/children/add")
    public String addChild(Authentication authentication,
                           @ModelAttribute("newChild") Child child) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();

        String username = parentService.generateChildUsername(parent, child);
        String rawPassword = parentService.generateRandomPassword();

        child.setUsername(username);
        child.setPassword(passwordEncoder.encode(rawPassword));
        child.setParent(parent);

        // Default goals (match entity!)
        if (child.getDigitGoal() == null) {
            child.setDigitGoal(10);
        }

        if (child.getAlphabetGoal() == null) {
            child.setAlphabetGoal(26);
        }

        childRepository.save(child);

        emailService.sendSimpleEmail(
                parent.getEmail(),
                "New Child Account Created",
                "Child Name: " + child.getChildName() +
                        "\nUsername: " + username +
                        "\nPassword: " + rawPassword
        );

        return "redirect:/parent/dashboard";
    }

    // ================= VIEW PROGRESS =================

    @GetMapping("/children/{childId}/progress")
    public String viewChildProgress(@PathVariable Long childId,
                                    @RequestParam(defaultValue = "NUMBER") LearningType type,
                                    Authentication authentication,
                                    Model model) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();
        Child child = childRepository.findById(childId).orElseThrow();

        if (!child.getParent().getId().equals(parent.getId())) {
            return "redirect:/parent/dashboard";
        }

        List<Progress> progressList =
                progressRepository.findByChildAndLearningType(child, type);

        long totalAttempts = progressList.size();
        long correctCount = progressList.stream().filter(Progress::isCorrect).count();
        double accuracy = totalAttempts == 0 ? 0 :
                (correctCount * 100.0 / totalAttempts);
        
        long totalTimeMs = progressList.stream()
            .map(Progress::getTimeTakenMs)
            .filter(java.util.Objects::nonNull)
            .mapToLong(Long::longValue)
            .sum();

        long attemptsWithTime = progressList.stream()
                .map(Progress::getTimeTakenMs)
                .filter(java.util.Objects::nonNull)
                .count();

        Long avgTimeMs = (attemptsWithTime == 0) ? 0L : (totalTimeMs / attemptsWithTime);


        model.addAttribute("child", child);
        model.addAttribute("parent", parent);
        model.addAttribute("progressList", progressList);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("accuracy", accuracy);
        model.addAttribute("learningType", type);
        model.addAttribute("avgTimeMs", avgTimeMs);

        return "child-progress";
    }

    // ================= UPDATE GOAL =================

    @PostMapping("/children/{childId}/goal")
    public String updateChildGoal(@PathVariable Long childId,
                                  @RequestParam Integer goalCount,
                                  @RequestParam LearningType type,
                                  Authentication authentication) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();
        Child child = childRepository.findById(childId).orElseThrow();

        if (!child.getParent().getId().equals(parent.getId())) {
            return "redirect:/parent/dashboard";
        }

        if (goalCount != null && goalCount > 0) {

            if (type == LearningType.NUMBER) {
                child.setDigitGoal(goalCount);
            } else {
                child.setAlphabetGoal(goalCount);
            }

            childRepository.save(child);
        }

        return "redirect:/parent/children/" + childId + "/progress?type=" + type;
    }

    // ================= REWARD VIDEOS =================

    @PostMapping("/reward-videos/add")
    public String addRewardVideo(@ModelAttribute("newRewardVideo") RewardVideo rewardVideo,
                                 Authentication authentication) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();

        rewardVideo.setParent(parent);

        if (rewardVideo.getTitle() == null || rewardVideo.getTitle().isBlank()) {
            rewardVideo.setTitle("Reward Video");
        }

        rewardVideoRepository.save(rewardVideo);

        return "redirect:/parent/dashboard";
    }

    @PostMapping("/reward-videos/{id}/delete")
    public String deleteRewardVideo(@PathVariable Long id,
                                    Authentication authentication) {

        String email = authentication.getName();
        Parent parent = parentRepository.findByEmail(email).orElseThrow();

        RewardVideo video = rewardVideoRepository.findById(id).orElse(null);

        if (video != null &&
                video.getParent().getId().equals(parent.getId())) {
            rewardVideoRepository.delete(video);
        }

        return "redirect:/parent/dashboard";
    }
}
