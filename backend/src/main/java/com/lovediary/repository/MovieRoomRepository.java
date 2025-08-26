package com.lovediary.repository;

import com.lovediary.entity.MovieRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRoomRepository extends JpaRepository<MovieRoom, Long> {
    
    /**
     * 根据房间代码查找房间
     */
    Optional<MovieRoom> findByRoomCode(String roomCode);
    
    /**
     * 检查房间代码是否存在
     */
    boolean existsByRoomCode(String roomCode);
    
    /**
     * 根据创建者ID查找房间，按创建时间倒序排列
     */
    List<MovieRoom> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
    
    /**
     * 根据电影ID查找房间
     */
    List<MovieRoom> findByMovieId(Long movieId);
    
    /**
     * 根据创建者ID和电影ID查找房间
     */
    List<MovieRoom> findByCreatorIdAndMovieId(Long creatorId, Long movieId);
}
