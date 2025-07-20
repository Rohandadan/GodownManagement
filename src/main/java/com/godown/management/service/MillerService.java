package com.godown.management.service;

import com.godown.management.model.Miller;
import com.godown.management.repository.MillerRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MillerService {
    
    @Autowired
    private MillerRepository millerRepository;
    
    public Miller findByMillerCode(String millerCode) {
        return millerRepository.findByMillerCode(millerCode);
    }
    
    public boolean existsByMillerCode(String millerCode) {
        return millerRepository.existsById(millerCode);
    }
    
    public Miller saveMiller(Miller miller) {
        return millerRepository.save(miller);
    }
    public List<Miller> findAllMillers() {
        return millerRepository.findAll();
    }
}