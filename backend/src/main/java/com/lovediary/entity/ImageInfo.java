package com.lovediary.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "diary_images")
public class ImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diary_id", nullable = false)
    private Long diaryId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;
}
