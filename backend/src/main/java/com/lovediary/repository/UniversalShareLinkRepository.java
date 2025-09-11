package com.lovediary.repository;

import com.lovediary.entity.UniversalShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UniversalShareLinkRepository extends JpaRepository<UniversalShareLink, Long> {
    
    /**
     * 根据分享token和类型查找活跃的分享链接
     */
    Optional<UniversalShareLink> findByShareTokenAndShareTypeAndIsActiveTrue(String shareToken, UniversalShareLink.ShareType shareType);
    
    /**
     * 根据分享token查找活跃的分享链接（兼容旧API）
     */
    Optional<UniversalShareLink> findByShareTokenAndIsActiveTrue(String shareToken);
    
    /**
     * 根据目标ID和类型查找活跃的分享链接
     */
    List<UniversalShareLink> findByTargetIdAndShareTypeAndIsActiveTrue(Long targetId, UniversalShareLink.ShareType shareType);
    
    /**
     * 根据目标ID查找活跃的分享链接（兼容旧API）
     */
    List<UniversalShareLink> findByTargetIdAndIsActiveTrue(Long targetId);
    
    /**
     * 查找过期的分享链接
     */
    @Query("SELECT usl FROM UniversalShareLink usl WHERE usl.expiresAt < :now AND usl.isActive = true")
    List<UniversalShareLink> findExpiredLinks(@Param("now") LocalDateTime now);
    
    /**
     * 根据目标ID和类型查找未过期的活跃分享链接
     */
    @Query("SELECT usl FROM UniversalShareLink usl WHERE usl.targetId = :targetId AND usl.shareType = :shareType AND usl.expiresAt > :now AND usl.isActive = true")
    List<UniversalShareLink> findActiveLinksByTargetIdAndType(@Param("targetId") Long targetId, @Param("shareType") UniversalShareLink.ShareType shareType, @Param("now") LocalDateTime now);
    
    /**
     * 根据目标ID查找未过期的活跃分享链接（兼容旧API）
     */
    @Query("SELECT usl FROM UniversalShareLink usl WHERE usl.targetId = :targetId AND usl.expiresAt > :now AND usl.isActive = true")
    List<UniversalShareLink> findActiveLinksByTargetId(@Param("targetId") Long targetId, @Param("now") LocalDateTime now);
    
    /**
     * 根据类型查找过期的分享链接
     */
    @Query("SELECT usl FROM UniversalShareLink usl WHERE usl.shareType = :shareType AND usl.expiresAt < :now AND usl.isActive = true")
    List<UniversalShareLink> findExpiredLinksByType(@Param("shareType") UniversalShareLink.ShareType shareType, @Param("now") LocalDateTime now);
}
