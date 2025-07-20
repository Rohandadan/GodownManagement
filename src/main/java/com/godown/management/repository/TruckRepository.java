package com.godown.management.repository;

import com.godown.management.model.Truck;
import com.godown.management.model.Miller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    List<Truck> findByMiller(Miller miller);
    List<Truck> findByUnloadingDateBetween(LocalDate startDate, LocalDate endDate);
    List<Truck> findByTruckName(String truckName);
    int countByMiller(Miller miller);
    void deleteAllByMiller(Miller miller);
}