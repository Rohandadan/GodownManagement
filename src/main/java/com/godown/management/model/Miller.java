package com.godown.management.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Miller {
    @Id
    private String millerCode;
    private String millerName;
    private int blockCapacity; // 12 or 16 trucks
    
    @OneToOne
    private Block assignedBlock;
    
    @OneToMany(mappedBy = "miller")
    private List<Truck> trucks;
    
    // Getters and Setters
    public String getMillerCode() {
        return millerCode;
    }
    
    public void setMillerCode(String millerCode) {
        this.millerCode = millerCode;
    }
    
    public String getMillerName() {
        return millerName;
    }
    
    public void setMillerName(String millerName) {
        this.millerName = millerName;
    }
    
    public int getBlockCapacity() {
        return blockCapacity;
    }
    
    public void setBlockCapacity(int blockCapacity) {
        this.blockCapacity = blockCapacity;
    }
    
    public Block getAssignedBlock() {
        return assignedBlock;
    }
    
    public void setAssignedBlock(Block assignedBlock) {
        this.assignedBlock = assignedBlock;
    }
    
    public List<Truck> getTrucks() {
        return trucks;
    }
    
    public void setTrucks(List<Truck> trucks) {
        this.trucks = trucks;
    }
}