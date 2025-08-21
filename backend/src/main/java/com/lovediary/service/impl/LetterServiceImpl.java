package com.lovediary.service.impl;

import com.lovediary.dto.CreateLetterRequest;
import com.lovediary.dto.LetterDTO;
import com.lovediary.entity.Letter;
import com.lovediary.entity.User;
import com.lovediary.repository.LetterRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LetterServiceImpl implements LetterService {

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LetterDTO createLetter(CreateLetterRequest request, Long senderId) {
        // 验证发送者是否存在
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));

        // 验证接收者是否存在
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("接收者不存在"));

        // 验证是否为伴侣关系或给自己写信
        if (!sender.getId().equals(receiver.getId()) && !isPartner(sender, receiver)) {
            throw new RuntimeException("只能给伴侣写信，或者给自己写信");
        }

        // 创建信件
        Letter letter = new Letter();
        letter.setTitle(request.getTitle());
        letter.setContent(request.getContent());
        letter.setUnlockTime(request.getUnlockTime());
        letter.setSender(sender);
        letter.setReceiver(receiver);
        letter.setIsRead(false);

        System.out.println("创建信件 - 发送者ID: " + sender.getId() + ", 接收者ID: " + receiver.getId());
        Letter savedLetter = letterRepository.save(letter);
        System.out.println("信件保存成功，ID: " + savedLetter.getId());
        return convertToDTO(savedLetter);
    }

    @Override
    public List<LetterDTO> getSentLetters(Long userId) {
        System.out.println("查询发送的信件，用户ID: " + userId);
        
        // 先检查数据库中是否有信件数据
        List<Letter> allLetters = letterRepository.findAll();
        System.out.println("数据库中总信件数量: " + allLetters.size());
        for (Letter letter : allLetters) {
            System.out.println("所有信件 - ID: " + letter.getId() + ", 标题: " + letter.getTitle() + 
                             ", 发送者ID: " + letter.getSender().getId() + 
                             ", 接收者ID: " + letter.getReceiver().getId());
        }
        
        List<Letter> letters = letterRepository.findBySenderIdOrderByCreatedAtDesc(userId);
        System.out.println("找到信件数量: " + letters.size());
        for (Letter letter : letters) {
            System.out.println("信件ID: " + letter.getId() + ", 标题: " + letter.getTitle() + ", 发送者ID: " + letter.getSender().getId());
        }
        return letters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LetterDTO> getReceivedLetters(Long userId) {
        List<Letter> letters = letterRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        return letters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LetterDTO> getUnlockedLetters(Long userId) {
        List<Letter> letters = letterRepository.findUnlockedLettersByReceiverId(userId);
        return letters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LetterDTO> getLockedLetters(Long userId) {
        List<Letter> letters = letterRepository.findLockedLettersByReceiverId(userId);
        return letters.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LetterDTO getLetterById(Long letterId, Long userId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("信件不存在"));

        // 验证用户是否有权限查看此信件
        if (!letter.getSender().getId().equals(userId) && !letter.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("无权限查看此信件");
        }

        // 如果是接收者且信件已解锁，标记为已读
        if (letter.getReceiver().getId().equals(userId) && 
            letter.getUnlockTime().isBefore(LocalDateTime.now()) && 
            !letter.getIsRead()) {
            letter.setIsRead(true);
            letterRepository.save(letter);
        }

        return convertToDTO(letter);
    }

    @Override
    public void markAsRead(Long letterId, Long userId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("信件不存在"));

        if (!letter.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("无权限操作此信件");
        }

        letter.setIsRead(true);
        letterRepository.save(letter);
    }

    @Override
    public void deleteLetter(Long letterId, Long userId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("信件不存在"));

        if (!letter.getSender().getId().equals(userId)) {
            throw new RuntimeException("只能删除自己发送的信件");
        }

        letterRepository.delete(letter);
    }

    private LetterDTO convertToDTO(Letter letter) {
        LetterDTO dto = new LetterDTO();
        dto.setId(letter.getId());
        dto.setTitle(letter.getTitle());
        dto.setContent(letter.getContent());
        dto.setUnlockTime(letter.getUnlockTime());
        dto.setSenderId(letter.getSender().getId());
        dto.setSenderName(letter.getSender().getDisplayName());
        dto.setReceiverId(letter.getReceiver().getId());
        dto.setReceiverName(letter.getReceiver().getDisplayName());
        dto.setIsRead(letter.getIsRead());
        dto.setCreatedAt(letter.getCreatedAt());
        dto.setUpdatedAt(letter.getUpdatedAt());

        // 计算是否已解锁
        LocalDateTime now = LocalDateTime.now();
        boolean isUnlocked = letter.getUnlockTime().isBefore(now);
        dto.setIsUnlocked(isUnlocked);

        // 计算剩余解锁时间
        if (!isUnlocked) {
            long remainingSeconds = ChronoUnit.SECONDS.between(now, letter.getUnlockTime());
            dto.setRemainingSeconds(remainingSeconds);
        } else {
            dto.setRemainingSeconds(0L);
        }

        return dto;
    }

    private boolean isPartner(User user1, User user2) {
        // 检查是否为伴侣关系
        return user1.getPartnerId() != null && user1.getPartnerId().equals(user2.getId()) ||
               user2.getPartnerId() != null && user2.getPartnerId().equals(user1.getId());
    }
}
