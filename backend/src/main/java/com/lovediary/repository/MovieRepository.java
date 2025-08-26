package com.lovediary.repository;

import com.lovediary.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    // 获取用户自己创建的电影
    List<Movie> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 获取用户可以查看的电影（包括自己的、伴侣的和公开的）
    @Query("SELECT m FROM Movie m WHERE m.user.id = ?1 OR m.partner.id = ?1 OR m.isPublic = true ORDER BY m.createdAt DESC")
    List<Movie> findViewableMoviesByUserId(Long userId);
    
    // 获取公开的电影
    List<Movie> findByIsPublicTrueOrderByCreatedAtDesc();
    
    // 分页查询用户自己创建的电影
    Page<Movie> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 分页查询用户可以查看的电影
    @Query("SELECT m FROM Movie m WHERE m.user.id = ?1 OR m.partner.id = ?1 OR m.isPublic = true ORDER BY m.createdAt DESC")
    Page<Movie> findViewableMoviesByUserId(Long userId, Pageable pageable);
    
    // 根据标题搜索用户可以查看的电影
    @Query("SELECT m FROM Movie m WHERE (m.user.id = ?1 OR m.partner.id = ?1 OR m.isPublic = true) AND m.title LIKE %?2% ORDER BY m.createdAt DESC")
    List<Movie> findViewableMoviesByUserIdAndTitleContaining(Long userId, String title);
}

