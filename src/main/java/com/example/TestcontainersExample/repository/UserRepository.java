package com.example.TestcontainersExample.repository;

import com.example.TestcontainersExample.model.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<TestUser, Long> {
}