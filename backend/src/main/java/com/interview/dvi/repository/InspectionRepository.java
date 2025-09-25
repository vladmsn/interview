package com.interview.dvi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.dvi.model.entities.Inspection;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface InspectionRepository extends JpaRepository<Inspection, Integer> {
    Page<Inspection> findByVin(String vin, Pageable pageable);
}
