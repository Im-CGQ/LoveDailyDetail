package com.lovediary.service;

import com.lovediary.dto.MovieDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {
    
    /**
     * 获取所有可观看的电影
     */
    List<MovieDTO> getAllViewableMovies(Long userId);
    
    /**
     * 获取公开电影
     */
    List<MovieDTO> getPublicMovies();
    
    /**
     * 获取我的电影
     */
    List<MovieDTO> getMyMovies(Long userId);
    
    /**
     * 根据ID获取电影详情
     */
    MovieDTO getMovieById(Long id, Long userId);
    
    /**
     * 创建电影
     */
    MovieDTO createMovie(MovieDTO movieDTO, Long userId);
    
    /**
     * 更新电影
     */
    MovieDTO updateMovie(Long id, MovieDTO movieDTO, Long userId);
    
    /**
     * 删除电影
     */
    void deleteMovie(Long id, Long userId);
    
    /**
     * 搜索电影
     */
    List<MovieDTO> searchMovies(String keyword, Long userId);
    
    /**
     * 分页获取电影
     */
    Page<MovieDTO> getMoviesByPage(Long userId, Pageable pageable);
    
    /**
     * 检查用户是否可以查看电影
     */
    boolean canViewMovie(Long movieId, Long userId);
    
    /**
     * 检查用户是否可以编辑电影
     */
    boolean canEditMovie(Long movieId, Long userId);
}
