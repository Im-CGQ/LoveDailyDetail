package com.lovediary.repository;

import com.lovediary.entity.ChatRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<ChatRecord> findByUser_IdOrderByDateDesc(Long userId, Pageable pageable);

    List<ChatRecord> findByUser_IdOrUser_IdOrderByDateDesc(Long userId1, Long userId2);

    Optional<ChatRecord> findByDateAndUser_Id(LocalDate date, Long userId);

    List<ChatRecord> findByDateAndUser_IdOrderByDateDesc(LocalDate date, Long userId);

    @Query("SELECT c FROM ChatRecord c WHERE (c.date = :date1 AND c.user.id = :userId1) OR (c.date = :date2 AND c.user.id = :userId2) ORDER BY c.date DESC")
    List<ChatRecord> findByDateAndUser_IdOrDateAndUser_IdOrderByDateDesc(@Param("date1") LocalDate date1, @Param("userId1") Long userId1, @Param("date2") LocalDate date2, @Param("userId2") Long userId2);

    List<ChatRecord> findByUser_IdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM ChatRecord c WHERE (c.user.id = :userId1 AND c.date BETWEEN :startDate1 AND :endDate1) OR (c.user.id = :userId2 AND c.date BETWEEN :startDate2 AND :endDate2) ORDER BY c.date DESC")
    List<ChatRecord> findByUser_IdAndDateBetweenOrUser_IdAndDateBetweenOrderByDateDesc(@Param("userId1") Long userId1, @Param("startDate1") LocalDate startDate1, @Param("endDate1") LocalDate endDate1, @Param("userId2") Long userId2, @Param("startDate2") LocalDate startDate2, @Param("endDate2") LocalDate endDate2);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId AND c.date = :date")
    Integer getTotalDurationByDateAndUserId(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId")
    Integer getTotalDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId1 OR c.user.id = :userId2")
    Integer getTotalDurationByUserIdOrUserId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT c.chatType, SUM(c.durationMinutes) as totalDuration FROM ChatRecord c WHERE c.user.id = :userId GROUP BY c.chatType ORDER BY totalDuration DESC")
    List<Object[]> getDurationByChatType(@Param("userId") Long userId);

    @Query("SELECT c.chatType, SUM(c.durationMinutes) as totalDuration FROM ChatRecord c WHERE c.user.id = :userId1 OR c.user.id = :userId2 GROUP BY c.chatType ORDER BY totalDuration DESC")
    List<Object[]> getDurationByChatTypeForUserOrPartner(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // 直接使用partner_id字段的查询方法（类似日记的实现）
    @Query("SELECT c FROM ChatRecord c WHERE c.user.id = :userId OR c.partner.id = :userId ORDER BY c.date DESC")
    List<ChatRecord> findByUserIdOrPartnerIdOrderByDateDesc(@Param("userId") Long userId);

    @Query("SELECT c FROM ChatRecord c WHERE (c.user.id = :userId OR c.partner.id = :userId) AND c.date = :date ORDER BY c.date DESC")
    List<ChatRecord> findByUserIdOrPartnerIdAndDateOrderByDateDesc(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT c FROM ChatRecord c WHERE (c.user.id = :userId OR c.partner.id = :userId) AND c.date BETWEEN :startDate AND :endDate ORDER BY c.date DESC")
    List<ChatRecord> findByUserIdOrPartnerIdAndDateBetweenOrderByDateDesc(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE c.user.id = :userId OR c.partner.id = :userId")
    Integer getTotalDurationByUserIdOrPartnerId(@Param("userId") Long userId);

    @Query("SELECT SUM(c.durationMinutes) FROM ChatRecord c WHERE (c.user.id = :userId OR c.partner.id = :userId) AND c.date = :date")
    Integer getTotalDurationByUserIdOrPartnerIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT c.chatType, SUM(c.durationMinutes) as totalDuration FROM ChatRecord c WHERE c.user.id = :userId OR c.partner.id = :userId GROUP BY c.chatType ORDER BY totalDuration DESC")
    List<Object[]> getDurationByChatTypeForUserIdOrPartnerId(@Param("userId") Long userId);
}
