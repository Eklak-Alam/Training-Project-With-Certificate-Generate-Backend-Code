package com.lic.repository;


import com.lic.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByPanNumber(String panNumber);
    boolean existsByPanNumber(String panNumber);
}