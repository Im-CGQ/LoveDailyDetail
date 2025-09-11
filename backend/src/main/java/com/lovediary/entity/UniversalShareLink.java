package com.lovediary.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "universal_share_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversalShareLink {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "share_type", nullable = false)
    private ShareType shareType;
    
    @Column(name = "target_id", nullable = false)
    private Long targetId;
    
    @Column(name = "share_token", nullable = false, unique = true)
    private String shareToken;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum ShareType {
        DIARY("日记"),
        LETTER("信件"),
        MOVIE("电影"),
        CHAT_RECORD("聊天记录"),
        MUSIC("音乐");
        
        private final String description;
        
        ShareType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
