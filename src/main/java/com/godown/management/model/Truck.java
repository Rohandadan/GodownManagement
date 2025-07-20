package com.godown.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String truckName;
    private String driverName;
    private LocalDate arrivalDate;
    private LocalDate unloadingDate;
    private double netWeight;
    private String riceType; // Boiled or Raw
    private String inspectorName;
    private int gunnyBags;
    private double charges; // 15 * gunnyBags
    
    @ManyToOne
    @JoinColumn(name = "miller_code")
    private Miller miller;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTruckName() {
        return truckName;
    }
    
    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public LocalDate getArrivalDate() {
        return arrivalDate;
    }
    
    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
    
    public LocalDate getUnloadingDate() {
        return unloadingDate;
    }
    
    public void setUnloadingDate(LocalDate unloadingDate) {
        this.unloadingDate = unloadingDate;
    }
    
    public double getNetWeight() {
        return netWeight;
    }
    
    public void setNetWeight(double netWeight) {
        this.netWeight = netWeight;
    }
    
    public String getRiceType() {
        return riceType;
    }
    
    public void setRiceType(String riceType) {
        this.riceType = riceType;
    }
    
    public String getInspectorName() {
        return inspectorName;
    }
    
    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }
    
    public int getGunnyBags() {
        return gunnyBags;
    }
    
    public void setGunnyBags(int gunnyBags) {
        this.gunnyBags = gunnyBags;
    }
    
    public double getCharges() {
        return charges;
    }
    
    public void setCharges(double charges) {
        this.charges = charges;
    }
    
    public Miller getMiller() {
        return miller;
    }
    
    public void setMiller(Miller miller) {
        this.miller = miller;
    }
}