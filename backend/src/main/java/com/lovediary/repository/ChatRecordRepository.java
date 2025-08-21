package com.lovediary.repository;

import com.lovediary.entity.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long> {

    List<ChatRecord> findByUser_IdOrderByDateDesc(Long userId);

    Optional<ChatRecord> findByDateAndUser_Id(LocalDate date, Long userId);

    List<ChatRecord> findByUser_IdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId AND c.date = :date")
    Integer getTotalDurationByDateAndUserId(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId")
    Integer getTotalDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT c.chatType, SUM(c.durationMinutes) as totalDuration FROM ChatRecord c WHERE c.user.id = :userId GROUP BY c.chatType ORDER BY totalDuration DESC")
    List<Object[]> getDurationByChatType(@Param("userId") Long userId);
}
