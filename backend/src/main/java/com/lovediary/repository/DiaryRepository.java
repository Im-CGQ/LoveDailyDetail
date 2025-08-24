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
    
    // 分页查询用户自己创建的日记（用于后台管理）
    Page<Diary> findOwnDiariesByUserIdOrderByDateDesc(Long userId, Pageable pageable);
    
    // 查询用户或伴侣的日记
    @Query("SELECT d FROM Diary d WHERE d.user.id = ?1 OR d.user.id = ?2 ORDER BY d.date DESC")
    List<Diary> findByUserIdOrUserIdOrderByDateDesc(Long userId1, Long userId2);
    
    // 查询用户可以查看的日记（包括自己的和伴侣的）
    @Query("SELECT d FROM Diary d WHERE d.user.id = ?1 OR d.partner.id = ?1 ORDER BY d.date DESC")
    List<Diary> findViewableDiariesByUserId(Long userId);
    
    // 查询用户自己创建的日记（用于后台管理）
    @Query("SELECT d FROM Diary d WHERE d.user.id = ?1 ORDER BY d.date DESC")
    List<Diary> findOwnDiariesByUserId(Long userId);
    
    // 按日期查询用户可以查看的日记
    @Query("SELECT d FROM Diary d WHERE d.date = ?1 AND (d.user.id = ?2 OR d.partner.id = ?2)")
    Optional<Diary> findViewableDiaryByDateAndUserId(LocalDate date, Long userId);
    
    // 按日期查询用户可以查看的日记列表
    @Query("SELECT d FROM Diary d WHERE d.date = ?1 AND (d.user.id = ?2 OR d.partner.id = ?2) ORDER BY d.createdAt DESC")
    List<Diary> findViewableDiariesByDateAndUserId(LocalDate date, Long userId);
    
    // 按日期范围查询用户可以查看的日记
    @Query("SELECT d FROM Diary d WHERE d.date BETWEEN ?1 AND ?2 AND (d.user.id = ?3 OR d.partner.id = ?3) ORDER BY d.date DESC")
    List<Diary> findViewableDiariesByDateRange(LocalDate startDate, LocalDate endDate, Long userId);
    
    // 按年月查询用户可以查看的日记
    @Query("SELECT d FROM Diary d WHERE YEAR(d.date) = ?1 AND MONTH(d.date) = ?2 AND (d.user.id = ?3 OR d.partner.id = ?3) ORDER BY d.date DESC")
    List<Diary> findViewableDiariesByYearAndMonth(int year, int month, Long userId);
} 