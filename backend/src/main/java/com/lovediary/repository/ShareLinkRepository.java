package com.lovediary.repository;

import com.lovediary.entity.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {
    
    Optional<ShareLink> findByShareTokenAndIsActiveTrue(String shareToken);
    
    List<ShareLink> findByDiaryIdAndIsActiveTrue(Long diaryId);
    
    @Query("SELECT sl FROM ShareLink sl WHERE sl.expiresAt < :now AND sl.isActive = true")
    List<ShareLink> findExpiredLinks(@Param("now") LocalDateTime now);
    
    @Query("SELECT sl FROM ShareLink sl WHERE sl.diaryId = :diaryId AND sl.expiresAt > :now AND sl.isActive = true")
    List<ShareLink> findActiveLinksByDiaryId(@Param("diaryId") Long diaryId, @Param("now") LocalDateTime now);
}
