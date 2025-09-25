package com.interview.dvi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.interview.dvi.model.entities.InspectionItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface InspectionItemRepository extends JpaRepository<InspectionItem, Integer> {
    @Modifying
    @Query("DELETE FROM InspectionItem i where i.id = :itemId")
    void hardDeleteByItemId(Integer itemId);

    @Modifying
    @Query("DELETE FROM InspectionItem i where i.inspection.id = :inspectionId")
    void hardDeleteAllByInspectionId(Integer inspectionId);

    Integer countInspectionItemById(Integer id);
    Page<InspectionItem> findByInspectionId(Integer inspectionId, Pageable pageable);
    Optional<InspectionItem> findByInspectionIdAndId(Integer inspectionId, Integer id);
}
