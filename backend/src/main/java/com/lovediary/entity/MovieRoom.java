package com.lovediary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "movie_rooms")
public class MovieRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Column(name = "room_code", unique = true, nullable = false)
    private String roomCode;

    @Column(name = "play_time", columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double playTime = 0.0; // 当前播放时间（秒）

    @Column(name = "is_playing")
    private Boolean isPlaying = false; // 是否正在播放

    @Column(name = "last_updated_by")
    private Long lastUpdatedBy; // 最后操作的用户ID

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private User creator;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieRoomMember> members = new ArrayList<>();
}

