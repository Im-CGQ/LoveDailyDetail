package com.lovediary.service;

import com.lovediary.dto.CreateLetterRequest;
import com.lovediary.dto.LetterDTO;
import java.util.List;

public interface LetterService {
    
    // 创建信件
    LetterDTO createLetter(CreateLetterRequest request, Long senderId);
    
    // 获取用户发送的信件列表
    List<LetterDTO> getSentLetters(Long userId);
    
    // 获取用户接收的信件列表（包括已解锁和未解锁的）
    List<LetterDTO> getReceivedLetters(Long userId);
    
    // 获取已解锁的信件列表
    List<LetterDTO> getUnlockedLetters(Long userId);
    
    // 获取未解锁的信件列表
    List<LetterDTO> getLockedLetters(Long userId);
    
    // 获取信件详情
    LetterDTO getLetterById(Long letterId, Long userId);
    
    // 标记信件为已读
    void markAsRead(Long letterId, Long userId);
    
    // 删除信件
    void deleteLetter(Long letterId, Long userId);
}
