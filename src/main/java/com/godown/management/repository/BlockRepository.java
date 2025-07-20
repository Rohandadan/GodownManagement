package com.godown.management.repository;

import com.godown.management.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByCompartmentNumberAndCapacityAndOccupied(int compartmentNumber, int capacity, boolean occupied);
    List<Block> findByOccupied(boolean occupied);
}
