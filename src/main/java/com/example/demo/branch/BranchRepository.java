package com.example.demo.branch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findBranchById(Long id);

    Optional<Branch> findBranchByTitle(String title);

    Optional<Branch> findBranchByTitleAndDescription(String title, String Description);

    boolean existsBranchByTitle(String title);

    boolean existsBranchByDescription(String description);

    boolean existsBranchByTitleAndDescription(String title, String description);

}
