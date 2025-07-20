package com.godown.management.service;

import com.godown.management.model.Truck;
import com.godown.management.model.Miller;
import com.godown.management.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TruckService {
    
    @Autowired
    private TruckRepository truckRepository;
    
    
    public Truck findById(Long id) {
        return truckRepository.findById(id).orElse(null);
    }
    
    public Truck saveTruck(Truck truck) {
        // Calculate charges based on gunny bags
        truck.setCharges(15 * truck.getGunnyBags());
        return truckRepository.save(truck);
    }
    
    public List<Truck> findByMiller(Miller miller) {
        return truckRepository.findByMiller(miller);
    }
    
    public List<Truck> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return truckRepository.findByUnloadingDateBetween(startDate, endDate);
    }
    
    public List<Truck> findByTruckName(String truckName) {
        return truckRepository.findByTruckName(truckName);
    }
    public int countTrucksByMiller(Miller miller) {
        return truckRepository.countByMiller(miller);
    }
    
    @Transactional
    public void deleteAllByMiller(Miller miller) {
        truckRepository.deleteAllByMiller(miller);
    }
    
    
}