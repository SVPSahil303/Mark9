package com.sahil.Mark9.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sahil.Mark9.model.Child;
import com.sahil.Mark9.model.Parent;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByParent(Parent parent);

    Optional<Child> findByUsername(String username);
}
