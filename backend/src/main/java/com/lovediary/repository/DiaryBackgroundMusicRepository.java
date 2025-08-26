package com.lovediary.repository;

import com.lovediary.entity.DiaryBackgroundMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryBackgroundMusicRepository extends JpaRepository<DiaryBackgroundMusic, Long> {
    List<DiaryBackgroundMusic> findByDiaryId(Long diaryId);
    void deleteByDiaryId(Long diaryId);
}
