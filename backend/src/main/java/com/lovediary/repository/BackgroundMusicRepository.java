package com.lovediary.repository;

import com.lovediary.entity.BackgroundMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackgroundMusicRepository extends JpaRepository<BackgroundMusic, Long> {
}
