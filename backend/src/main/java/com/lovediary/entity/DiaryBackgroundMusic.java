package com.lovediary.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "diary_background_music")
public class DiaryBackgroundMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_id", nullable = false)
    private Long diaryId;

    @Column(name = "music_url", nullable = false)
    private String musicUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "title")
    private String title;

    @Column(name = "artist")
    private String artist;

    @Column(name = "duration")
    private Integer duration;
}
