package com.lovediary.service.impl;

import com.lovediary.dto.ChatRecordDTO;
import com.lovediary.entity.ChatRecord;
import com.lovediary.entity.User;
import com.lovediary.repository.ChatRecordRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ChatRecord> getChatRecordsByUserId(Long userId) {
        return chatRecordRepository.findByUser_IdOrderByDateDesc(userId);
    }

    @Override
    public List<ChatRecord> getChatRecordsByUserIdAndPartner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getPartnerId() != null) {
            // 获取自己和伴侣的聊天记录
            return chatRecordRepository.findByUser_IdOrUser_IdOrderByDateDesc(userId, user.getPartnerId());
        } else {
            // 没有伴侣，只获取自己的聊天记录
            return chatRecordRepository.findByUser_IdOrderByDateDesc(userId);
        }
    }

    @Override
    public Page<ChatRecord> getOwnChatRecordsWithPaginationByUserId(Pageable pageable, Long userId) {
        return chatRecordRepository.findByUser_IdOrderByDateDesc(userId, pageable);
    }

    @Override
    public ChatRecord getChatRecordById(Long id) {
        return chatRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("聊天记录不存在"));
    }

    @Override
    public Optional<ChatRecord> getChatRecordByDateAndUserId(LocalDate date, Long userId) {
        return chatRecordRepository.findByDateAndUser_Id(date, userId);
    }

    @Override
    public List<ChatRecord> getChatRecordsByDateAndUserIdAndPartner(LocalDate date, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getPartnerId() != null) {
            // 获取自己和伴侣在指定日期的聊天记录
            return chatRecordRepository.findByDateAndUser_IdOrDateAndUser_IdOrderByDateDesc(date, userId, date, user.getPartnerId());
        } else {
            // 没有伴侣，只获取自己在指定日期的聊天记录
            return chatRecordRepository.findByDateAndUser_IdOrderByDateDesc(date, userId);
        }
    }

    @Override
    public ChatRecord createChatRecord(ChatRecordDTO chatRecordDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setChatType(chatRecordDTO.getChatType());
        chatRecord.setDurationMinutes(chatRecordDTO.getDurationMinutes());
        chatRecord.setDate(chatRecordDTO.getDate());
        chatRecord.setDescription(chatRecordDTO.getDescription());
        chatRecord.setCustomType(chatRecordDTO.getCustomType());
        chatRecord.setUser(user);
        
        // 设置伴侣ID
        if (user.getPartnerId() != null) {
            User partner = userRepository.findById(user.getPartnerId())
                    .orElse(null);
            chatRecord.setPartner(partner);
        }

        return chatRecordRepository.save(chatRecord);
    }

    @Override
    public ChatRecord updateChatRecord(Long id, ChatRecordDTO chatRecordDTO) {
        ChatRecord existingRecord = getChatRecordById(id);
        
        existingRecord.setChatType(chatRecordDTO.getChatType());
        existingRecord.setDurationMinutes(chatRecordDTO.getDurationMinutes());
        existingRecord.setDate(chatRecordDTO.getDate());
        existingRecord.setDescription(chatRecordDTO.getDescription());
        existingRecord.setCustomType(chatRecordDTO.getCustomType());

        return chatRecordRepository.save(existingRecord);
    }

    @Override
    public void deleteChatRecord(Long id) {
        if (!chatRecordRepository.existsById(id)) {
            throw new RuntimeException("聊天记录不存在");
        }
        chatRecordRepository.deleteById(id);
    }

    @Override
    public List<ChatRecord> getChatRecordsByDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        return chatRecordRepository.findByUser_IdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }

    @Override
    public List<ChatRecord> getChatRecordsByDateRangeAndPartner(LocalDate startDate, LocalDate endDate, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getPartnerId() != null) {
            // 获取自己和伴侣在指定日期范围的聊天记录
            return chatRecordRepository.findByUser_IdAndDateBetweenOrUser_IdAndDateBetweenOrderByDateDesc(userId, startDate, endDate, user.getPartnerId(), startDate, endDate);
        } else {
            // 没有伴侣，只获取自己在指定日期范围的聊天记录
            return chatRecordRepository.findByUser_IdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
        }
    }

    @Override
    public Integer getTotalDurationByDateAndUserId(LocalDate date, Long userId) {
        Integer totalDuration = chatRecordRepository.getTotalDurationByDateAndUserId(date, userId);
        return totalDuration != null ? totalDuration : 0;
    }

    @Override
    public Integer getTotalDurationByUserId(Long userId) {
        Integer totalDuration = chatRecordRepository.getTotalDurationByUserId(userId);
        return totalDuration != null ? totalDuration : 0;
    }

    @Override
    public Integer getTotalDurationByUserIdAndPartner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getPartnerId() != null) {
            // 获取自己和伴侣的总聊天时长
            Integer totalDuration = chatRecordRepository.getTotalDurationByUserIdOrUserId(userId, user.getPartnerId());
            return totalDuration != null ? totalDuration : 0;
        } else {
            // 没有伴侣，只获取自己的总聊天时长
            return getTotalDurationByUserId(userId);
        }
    }

    @Override
    public Map<String, Integer> getDurationByChatType(Long userId) {
        List<Object[]> results = chatRecordRepository.getDurationByChatType(userId);
        Map<String, Integer> durationByType = new HashMap<>();
        
        for (Object[] result : results) {
            String chatType = (String) result[0];
            Integer totalDuration = ((Number) result[1]).intValue();
            durationByType.put(chatType, totalDuration);
        }
        
        return durationByType;
    }

    @Override
    public Map<String, Integer> getDurationByChatTypeAndPartner(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getPartnerId() != null) {
            // 获取自己和伴侣的聊天类型时长统计
            List<Object[]> results = chatRecordRepository.getDurationByChatTypeForUserOrPartner(userId, user.getPartnerId());
            Map<String, Integer> durationByType = new HashMap<>();
            
            for (Object[] result : results) {
                String chatType = (String) result[0];
                Integer totalDuration = ((Number) result[1]).intValue();
                durationByType.put(chatType, totalDuration);
            }
            
            return durationByType;
        } else {
            // 没有伴侣，只获取自己的聊天类型时长统计
            return getDurationByChatType(userId);
        }
    }

    @Override
    public boolean existsByDateAndUserId(LocalDate date, Long userId) {
        return chatRecordRepository.findByDateAndUser_Id(date, userId).isPresent();
    }
}
