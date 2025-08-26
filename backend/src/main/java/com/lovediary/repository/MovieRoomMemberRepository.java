package com.lovediary.repository;

import com.lovediary.entity.MovieRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovieRoomMemberRepository extends JpaRepository<MovieRoomMember, Long> {
    
    /**
     * 根据房间ID查找成员
     */
    List<MovieRoomMember> findByRoomId(Long roomId);
    
    /**
     * 根据用户ID查找成员
     */
    List<MovieRoomMember> findByUserId(Long userId);
    
    /**
     * 根据房间ID和用户ID查找成员
     */
    MovieRoomMember findByRoomIdAndUserId(Long roomId, Long userId);
    
    /**
     * 检查用户是否在房间中
     */
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
    
    /**
     * 根据房间ID和用户ID删除成员
     */
    @Modifying
    @Query("DELETE FROM MovieRoomMember m WHERE m.room.id = :roomId AND m.user.id = :userId")
    void deleteByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
    
    /**
     * 更新用户在线状态
     */
    @Modifying
    @Query("UPDATE MovieRoomMember m SET m.isOnline = :isOnline, m.lastSeen = :lastSeen WHERE m.room.id = :roomId AND m.user.id = :userId")
    void updateOnlineStatus(@Param("roomId") Long roomId, @Param("userId") Long userId, @Param("isOnline") boolean isOnline, @Param("lastSeen") LocalDateTime lastSeen);
    
    /**
     * 获取房间在线成员数量
     */
    @Query("SELECT COUNT(m) FROM MovieRoomMember m WHERE m.room.id = :roomId AND m.isOnline = true")
    long countOnlineMembersByRoomId(@Param("roomId") Long roomId);
}

