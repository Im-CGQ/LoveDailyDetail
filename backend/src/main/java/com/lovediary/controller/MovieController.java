package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.MovieDTO;
import com.lovediary.service.MovieService;
import com.lovediary.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final JwtUtil jwtUtil;

    /**
     * 获取所有可观看的电影
     */
    @GetMapping
    public ApiResponse<List<MovieDTO>> getAllMovies(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            List<MovieDTO> movies = movieService.getAllViewableMovies(userId);
            return ApiResponse.success(movies);
        } catch (Exception e) {
            return ApiResponse.error("获取电影列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取公开电影
     */
    @GetMapping("/public")
    public ApiResponse<List<MovieDTO>> getPublicMovies() {
        try {
            List<MovieDTO> movies = movieService.getPublicMovies();
            return ApiResponse.success(movies);
        } catch (Exception e) {
            return ApiResponse.error("获取公开电影失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的电影
     */
    @GetMapping("/my")
    public ApiResponse<List<MovieDTO>> getMyMovies(HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            List<MovieDTO> movies = movieService.getMyMovies(userId);
            return ApiResponse.success(movies);
        } catch (Exception e) {
            return ApiResponse.error("获取我的电影失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取电影详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MovieDTO> getMovieById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            MovieDTO movie = movieService.getMovieById(id, userId);
            return ApiResponse.success(movie);
        } catch (Exception e) {
            return ApiResponse.error("获取电影详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建电影
     */
    @PostMapping
    public ApiResponse<MovieDTO> createMovie(@Valid @RequestBody MovieDTO movieDTO, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            MovieDTO createdMovie = movieService.createMovie(movieDTO, userId);
            return ApiResponse.success(createdMovie);
        } catch (Exception e) {
            return ApiResponse.error("创建电影失败: " + e.getMessage());
        }
    }

    /**
     * 更新电影
     */
    @PutMapping("/{id}")
    public ApiResponse<MovieDTO> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDTO, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            MovieDTO updatedMovie = movieService.updateMovie(id, movieDTO, userId);
            return ApiResponse.success(updatedMovie);
        } catch (Exception e) {
            return ApiResponse.error("更新电影失败: " + e.getMessage());
        }
    }

    /**
     * 删除电影
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            movieService.deleteMovie(id, userId);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除电影失败: " + e.getMessage());
        }
    }

    /**
     * 搜索电影
     */
    @GetMapping("/search")
    public ApiResponse<List<MovieDTO>> searchMovies(@RequestParam String keyword, HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            List<MovieDTO> movies = movieService.searchMovies(keyword, userId);
            return ApiResponse.success(movies);
        } catch (Exception e) {
            return ApiResponse.error("搜索电影失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取电影
     */
    @GetMapping("/page")
    public ApiResponse<Page<MovieDTO>> getMoviesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<MovieDTO> movies = movieService.getMoviesByPage(userId, pageable);
            return ApiResponse.success(movies);
        } catch (Exception e) {
            return ApiResponse.error("获取电影分页失败: " + e.getMessage());
        }
    }
}
