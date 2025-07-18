package com.society.maintenance.service;

import com.society.maintenance.model.FlatOwner;
import com.society.maintenance.repository.FlatOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FlatOwnerService {
    
    @Autowired
    private FlatOwnerRepository flatOwnerRepository;
    
    public List<FlatOwner> getAllActiveFlatOwners() {
        return flatOwnerRepository.findByIsActiveTrue();
    }
    
    public Optional<FlatOwner> getFlatOwnerById(Long id) {
        return flatOwnerRepository.findById(id);
    }
    
    public FlatOwner saveFlatOwner(FlatOwner flatOwner) {
        return flatOwnerRepository.save(flatOwner);
    }
    
    public FlatOwner updateFlatOwner(FlatOwner flatOwner) {
        return flatOwnerRepository.save(flatOwner);
    }
    
    public void deleteFlatOwner(Long id) {
        Optional<FlatOwner> flatOwner = flatOwnerRepository.findById(id);
        if (flatOwner.isPresent()) {
            flatOwner.get().setIsActive(false);
            flatOwnerRepository.save(flatOwner.get());
        }
    }
    
    public boolean existsByEmail(String email) {
        return flatOwnerRepository.existsByEmail(email);
    }
    
    public boolean existsByFlatNumber(String flatNumber) {
        return flatOwnerRepository.existsByFlatNumber(flatNumber);
    }
    
    public List<FlatOwner> searchByName(String name) {
        return flatOwnerRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<FlatOwner> getFlatOwnersByWing(String wing) {
        return flatOwnerRepository.findByWing(wing);
    }
    
    public Long getTotalActiveFlatOwners() {
        return flatOwnerRepository.countActiveFlatOwners();
    }
}