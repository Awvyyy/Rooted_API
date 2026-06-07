package com.example.demo.root;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RootRepository extends JpaRepository<Root, Long> {

    Optional<Root> findById(Long id);

    boolean existsById(Long id);

    Optional<Root> findByTitle(String title);

    boolean existsByTitle(String title);

    Optional<Root> findByDescription(String description);

    boolean existsByDescription(String description);
}
