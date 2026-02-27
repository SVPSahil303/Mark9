package com.sahil.Mark9.controller;

import com.sahil.Mark9.model.Child;
import com.sahil.Mark9.model.LearningType;
import com.sahil.Mark9.model.Progress;
import com.sahil.Mark9.repository.ChildRepository;
import com.sahil.Mark9.repository.ProgressRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/child/progress")
public class ProgressController {

    private final ChildRepository childRepository;
    private final ProgressRepository progressRepository;

    public ProgressController(ChildRepository childRepository,
                              ProgressRepository progressRepository) {
        this.childRepository = childRepository;
        this.progressRepository = progressRepository;
    }

    @PostMapping("/symbol")
    public Map<String, Object> saveProgress(
            @RequestParam String symbol,
            @RequestParam LearningType learningType,
            @RequestParam boolean correct,
            @RequestParam Long timeTakenMs,
            @RequestParam Integer tabSwitchCount,
            HttpSession session) {

        Long childId = (Long) session.getAttribute("LOGGED_IN_CHILD_ID");

        if (childId == null) {
            throw new RuntimeException("Child not logged in");
        }

        Child child = childRepository.findById(childId).orElseThrow();

        // ===== Save Attempt =====
        Progress progress = new Progress();
        progress.setChild(child);
        progress.setSymbol(symbol);
        progress.setLearningType(learningType);
        progress.setCorrect(correct);
        progress.setTimeTakenMs(timeTakenMs);
        progress.setTabSwitchCount(tabSwitchCount);

        progressRepository.save(progress);

        Map<String, Object> response = new HashMap<>();

        response.put("goalAchieved", false);

        if (!correct) {
            return response;
        }

        // ========================================
        // UPDATE CHILD STATE (CORRECT ATTEMPT)
        // ========================================

        if (learningType == LearningType.NUMBER) {

            int digit = Integer.parseInt(symbol);

            child.setLastDigitPracticed(digit);
            child.setTotalDigitsLearned(
                    child.getTotalDigitsLearned() + 1
            );

        } else {

            child.setLastAlphabetPracticed(symbol);
            child.setTotalAlphabetsLearned(
                    child.getTotalAlphabetsLearned() + 1
            );
        }

        childRepository.save(child);

        // ========================================
        // CHECK GOAL
        // ========================================

        int goal = (learningType == LearningType.NUMBER)
                ? child.getDigitGoal()
                : child.getAlphabetGoal();

        long correctCount = progressRepository
                .findByChildAndLearningType(child, learningType)
                .stream()
                .filter(Progress::isCorrect)
                .count();

        if (correctCount >= goal) {

            response.put("goalAchieved", true);
            response.put("previousGoal", goal);
            response.put("nextStartSymbol",
                    (learningType == LearningType.NUMBER) ? "0" : "A");

            return response;
        }

        // ========================================
        // CALCULATE NEXT SYMBOL
        // ========================================

        String nextSymbol = getNextSymbol(symbol, learningType);

        response.put("nextSymbol", nextSymbol);

        return response;
    }

    // ========================================
    // HELPER METHOD
    // ========================================

    private String getNextSymbol(String current, LearningType type) {

        if (type == LearningType.NUMBER) {

            int val = Integer.parseInt(current);
            return (val < 9) ? String.valueOf(val + 1) : null;

        } else {

            char c = current.charAt(0);
            return (c < 'Z') ? String.valueOf((char) (c + 1)) : null;
        }
    }
}
