package com.lovediary.repository;

import com.lovediary.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    Optional<Diary> findByDate(LocalDate date);
    
    List<Diary> findAllByOrderByDateDesc();
    
    // 用户相关的查询方法
    Optional<Diary> findByDateAndUserId(LocalDate date, Long userId);
    
    List<Diary> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT d FROM Diary d WHERE d.date BETWEEN ?1 AND ?2 ORDER BY d.date DESC")
    List<Diary> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT d FROM Diary d WHERE d.date BETWEEN ?1 AND ?2 AND d.user.id = ?3 ORDER BY d.date DESC")
    List<Diary> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, Long userId);
    
    boolean existsByDate(LocalDate date);
    
    boolean existsByDateAndUserId(LocalDate date, Long userId);
    
    @Query("SELECT d FROM Diary d WHERE YEAR(d.date) = ?1 AND MONTH(d.date) = ?2 ORDER BY d.date DESC")
    List<Diary> findByYearAndMonth(int year, int month);
    
    @Query("SELECT d FROM Diary d WHERE YEAR(d.date) = ?1 AND MONTH(d.date) = ?2 AND d.user.id = ?3 ORDER BY d.date DESC")
    List<Diary> findByYearAndMonthAndUserId(int year, int month, Long userId);
    
    @Query("SELECT d FROM Diary d ORDER BY d.date DESC")
    List<Diary> findTopNByOrderByDateDesc(int limit);
    
    // 用户分页查询
    Page<Diary> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
    
    // 查询用户或伴侣的日记
    @Query("SELECT d FROM Diary d WHERE d.user.id = ?1 OR d.user.id = ?2 ORDER BY d.date DESC")
    List<Diary> findByUserIdOrUserIdOrderByDateDesc(Long userId1, Long userId2);
} 