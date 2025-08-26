package com.lovediary.repository;

import com.lovediary.entity.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {
    List<ImageInfo> findByDiaryId(Long diaryId);
    void deleteByDiaryId(Long diaryId);
}
