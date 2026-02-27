package com.sahil.Mark9.repository;

import com.sahil.Mark9.model.Parent;
import com.sahil.Mark9.model.RewardVideo;
import com.sahil.Mark9.model.LearningType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardVideoRepository extends JpaRepository<RewardVideo, Long> {

    List<RewardVideo> findByParent(Parent parent);

    // 🔥 Type specific rewards
    List<RewardVideo> findByParentAndLearningType(
            Parent parent,
            LearningType learningType
    );
}
