package com.society.maintenance.repository;

import com.society.maintenance.model.FlatOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlatOwnerRepository extends JpaRepository<FlatOwner, Long> {
    
    Optional<FlatOwner> findByEmail(String email);
    
    Optional<FlatOwner> findByFlatNumber(String flatNumber);
    
    List<FlatOwner> findByIsActiveTrue();
    
    List<FlatOwner> findByWing(String wing);
    
    List<FlatOwner> findByFloor(Integer floor);
    
    @Query("SELECT f FROM FlatOwner f WHERE f.name LIKE %:name% AND f.isActive = true")
    List<FlatOwner> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT COUNT(f) FROM FlatOwner f WHERE f.isActive = true")
    Long countActiveFlatOwners();
    
    boolean existsByEmail(String email);
    
    boolean existsByFlatNumber(String flatNumber);
}