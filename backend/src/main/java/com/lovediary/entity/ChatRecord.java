package com.lovediary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_records")
public class ChatRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatType; // 聊天类型：微信语音、微信聊天、小红书聊天、自定义类型

    @Column(nullable = false)
    private Integer durationMinutes; // 聊天时长（分钟）

    @Column(nullable = false)
    private LocalDate date; // 聊天日期

    @Column(columnDefinition = "TEXT")
    private String description; // 聊天描述或备注

    @Column(name = "custom_type")
    private String customType; // 自定义聊天类型

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    @JsonIgnore
    private User partner;
}
