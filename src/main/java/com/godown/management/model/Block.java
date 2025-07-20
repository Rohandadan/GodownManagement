package com.godown.management.model;

import jakarta.persistence.*;

@Entity
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int compartmentNumber; // 1, 2, or 3
    private String blockName; // A, B, C, etc.
    private int capacity; // 12 or 16 trucks
    private boolean occupied;
    
    @OneToOne(mappedBy = "assignedBlock")
    private Miller miller;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public int getCompartmentNumber() {
        return compartmentNumber;
    }
    
    public void setCompartmentNumber(int compartmentNumber) {
        this.compartmentNumber = compartmentNumber;
    }
    
    public String getBlockName() {
        return blockName;
    }
    
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
    
    public Miller getMiller() {
        return miller;
    }
    
    public void setMiller(Miller miller) {
        this.miller = miller;
    }
    
    public String getFullName() {
        return "Compartment " + compartmentNumber + ", Block " + blockName;
    }
}
