package com.lovediary.repository;

import com.lovediary.entity.VideoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoInfoRepository extends JpaRepository<VideoInfo, Long> {
    List<VideoInfo> findByDiaryId(Long diaryId);
    void deleteByDiaryId(Long diaryId);
}
