package com.lovediary.service;

import com.lovediary.dto.ChatRecordDTO;
import com.lovediary.entity.ChatRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatRecordService {

    List<ChatRecord> getChatRecordsByUserId(Long userId);

    ChatRecord getChatRecordById(Long id);

    Optional<ChatRecord> getChatRecordByDateAndUserId(LocalDate date, Long userId);

    ChatRecord createChatRecord(ChatRecordDTO chatRecordDTO, Long userId);

    ChatRecord updateChatRecord(Long id, ChatRecordDTO chatRecordDTO);

    void deleteChatRecord(Long id);

    List<ChatRecord> getChatRecordsByDateRange(LocalDate startDate, LocalDate endDate, Long userId);

    Integer getTotalDurationByDateAndUserId(LocalDate date, Long userId);

    Integer getTotalDurationByUserId(Long userId);

    Map<String, Integer> getDurationByChatType(Long userId);

    boolean existsByDateAndUserId(LocalDate date, Long userId);
}
