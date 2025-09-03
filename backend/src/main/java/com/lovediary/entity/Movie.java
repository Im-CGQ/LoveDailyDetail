package com.lovediary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "movie_url")
    private String movieUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "duration")
    private Integer durationSeconds; // 新增：精确时长（秒）
    
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

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

