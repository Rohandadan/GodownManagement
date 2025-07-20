package com.godown.management.service;

import com.godown.management.model.Block;
import com.godown.management.model.Miller;
import com.godown.management.repository.BlockRepository;
import com.godown.management.repository.MillerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlockService {
    
    @Autowired
    private BlockRepository blockRepository;
    

    @Autowired
    private MillerRepository millerRepository;

    @Autowired
    private TruckService truckService;
    
    public List<Block> findAvailableBlocksByCapacity(int capacity) {
        return blockRepository.findByOccupied(false).stream()
                .filter(block -> block.getCapacity() == capacity)
                .toList();
    }
    
    public List<Block> findAvailableBlocksByCompartmentAndCapacity(int compartmentNumber, int capacity) {
        return blockRepository.findByCompartmentNumberAndCapacityAndOccupied(compartmentNumber, capacity, false);
    }
    
    public Block assignBlockToMiller(Long blockId) {
        Block block = blockRepository.findById(blockId).orElse(null);
        if (block != null) {
            block.setOccupied(true);
            return blockRepository.save(block);
        }
        return null;
    }
    
    public void initializeBlocks() {
        // Check if blocks already exist
        if (blockRepository.count() > 0) {
            return;
        }
        
        // Create blocks for all compartments
        String[] smallBlockNames = {"A", "B", "C", "D", "I", "J", "K", "L"};
        String[] largeBlockNames = {"E", "F", "G", "H", "M", "N", "O", "P"};
        
        for (int compartment = 1; compartment <= 3; compartment++) {
            // Create blocks that can hold 12 trucks
            for (String blockName : smallBlockNames) {
                Block block = new Block();
                block.setCompartmentNumber(compartment);
                block.setBlockName(blockName);
                block.setCapacity(12);
                block.setOccupied(false);
                blockRepository.save(block);
            }
            
            // Create blocks that can hold 16 trucks
            for (String blockName : largeBlockNames) {
                Block block = new Block();
                block.setCompartmentNumber(compartment);
                block.setBlockName(blockName);
                block.setCapacity(16);
                block.setOccupied(false);
                blockRepository.save(block);
            }
        }
    }

    public List<Block> findAllBlocks() {
        return blockRepository.findAll();
    }

    public Block findById(Long id) {
        return blockRepository.findById(id).orElse(null);
    }
    
    
    
    @Transactional
    public void clearBlockData(Long blockId) {
        Block block = findById(blockId);
        if (block != null && block.isOccupied()) {
            Miller miller = block.getMiller();
            if (miller != null) {
                // 1. Delete all trucks associated with the miller
                truckService.deleteAllByMiller(miller);

                // 2. Un-assign the block from miller (JPA requirement before deleting miller)
                miller.setAssignedBlock(null);
                millerRepository.save(miller);

                // 3. Delete the miller
                millerRepository.delete(miller);
            }
            
            // 4. Reset the block to be available
            block.setOccupied(false);
            block.setMiller(null); // Explicitly remove reference
            blockRepository.save(block);
        }
    }
    
	
	}