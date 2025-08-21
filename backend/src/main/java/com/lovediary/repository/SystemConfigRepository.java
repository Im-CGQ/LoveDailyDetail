package com.lovediary.repository;

import com.lovediary.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    Optional<SystemConfig> findByConfigKeyAndUserId(String configKey, Long userId);
    
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.userId = :userId OR sc.userId IS NULL")
    List<SystemConfig> findByUserIdOrGlobal(@Param("userId") Long userId);
    
    List<SystemConfig> findByUserId(Long userId);
    
    List<SystemConfig> findByUserIdIsNull();
}
