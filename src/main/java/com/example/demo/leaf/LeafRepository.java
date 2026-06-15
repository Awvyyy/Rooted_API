package com.example.demo.leaf;

import com.example.demo.branch.Branch;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeafRepository extends JpaRepository<Leaf, Long> {

    boolean existsByCommentaryAndUserAndBranch(String commentary, User user, Branch branch);

    Optional<Leaf> findLeafByCommentaryAndUserAndBranch(String commentary, User user, Branch branch);
}