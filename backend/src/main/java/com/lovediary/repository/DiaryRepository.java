package com.lovediary.repository;

import com.lovediary.entity.Diary;
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
    
    @Query("SELECT d FROM Diary d WHERE d.date BETWEEN ?1 AND ?2 ORDER BY d.date DESC")
    List<Diary> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    boolean existsByDate(LocalDate date);
    
    @Query("SELECT d FROM Diary d WHERE YEAR(d.date) = ?1 AND MONTH(d.date) = ?2 ORDER BY d.date DESC")
    List<Diary> findByYearAndMonth(int year, int month);
} 