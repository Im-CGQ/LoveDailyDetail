package com.lovediary.repository;

import com.lovediary.entity.LetterShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LetterShareLinkRepository extends JpaRepository<LetterShareLink, Long> {
    
    Optional<LetterShareLink> findByShareTokenAndIsActiveTrue(String shareToken);
    
    List<LetterShareLink> findByLetterIdAndIsActiveTrue(Long letterId);
    
    @Query("SELECT lsl FROM LetterShareLink lsl WHERE lsl.expiresAt < :now AND lsl.isActive = true")
    List<LetterShareLink> findExpiredLinks(@Param("now") LocalDateTime now);
    
    @Query("SELECT lsl FROM LetterShareLink lsl WHERE lsl.letterId = :letterId AND lsl.expiresAt > :now AND lsl.isActive = true")
    List<LetterShareLink> findActiveLinksByLetterId(@Param("letterId") Long letterId, @Param("now") LocalDateTime now);
}
