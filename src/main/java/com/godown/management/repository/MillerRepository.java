package com.godown.management.repository;

import com.godown.management.model.Miller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MillerRepository extends JpaRepository<Miller, String> {
    // Find by millerCode
    Miller findByMillerCode(String millerCode);
}