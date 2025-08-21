package com.lovediary.repository;

import com.lovediary.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterRepository extends JpaRepository<Letter, Long> {
    
    // 查找用户发送的信件
    List<Letter> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    
    // 查找用户接收的信件
    List<Letter> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    
    // 查找用户发送给特定接收者的信件
    List<Letter> findBySenderIdAndReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
    
    // 查找用户从特定发送者接收的信件
    List<Letter> findByReceiverIdAndSenderIdOrderByCreatedAtDesc(Long receiverId, Long senderId);
    
    // 查找已解锁的信件
    @Query("SELECT l FROM Letter l WHERE l.receiver.id = :receiverId AND l.unlockTime <= CURRENT_TIMESTAMP ORDER BY l.createdAt DESC")
    List<Letter> findUnlockedLettersByReceiverId(@Param("receiverId") Long receiverId);
    
    // 查找未解锁的信件
    @Query("SELECT l FROM Letter l WHERE l.receiver.id = :receiverId AND l.unlockTime > CURRENT_TIMESTAMP ORDER BY l.unlockTime ASC")
    List<Letter> findLockedLettersByReceiverId(@Param("receiverId") Long receiverId);
}
