package com.sahil.Mark9.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sahil.Mark9.model.Child;
import com.sahil.Mark9.model.Progress;
import com.sahil.Mark9.model.LearningType;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByChild(Child child);

    List<Progress> findByChildOrderByAttemptedAtDesc(Child child);

    // 🔥 Filter by NUMBER or ALPHABET
    List<Progress> findByChildAndLearningType(Child child, LearningType learningType);

    // 🔥 Ordered + filtered (for progress page)
    List<Progress> findByChildAndLearningTypeOrderByAttemptedAtDesc(
            Child child,
            LearningType learningType
    );
}
