package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.MovieDTO;
import com.lovediary.entity.User;
import com.lovediary.service.MovieService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieService movieService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Long getCurrentUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);
            User user = userService.findByUsername(username).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getAllMovies(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                // If not authenticated, return public movies only
                List<MovieDTO> publicMovies = movieService.getPublicMovies();
                return ResponseEntity.ok(ApiResponse.success("获取公开电影成功", publicMovies));
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<MovieDTO> moviesPage = movieService.getMoviesByPage(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取电影列表成功", moviesPage.getContent()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取电影列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getPublicMovies() {
        try {
            List<MovieDTO> movies = movieService.getPublicMovies();
            return ResponseEntity.ok(ApiResponse.success("获取公开电影成功", movies));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取公开电影失败：" + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getMyMovies(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieDTO> movies = movieService.getMyMovies(userId);
            return ResponseEntity.ok(ApiResponse.success("获取我的电影成功", movies));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取我的电影失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDTO>> getMovieById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            MovieDTO movie = movieService.getMovieById(id, userId);
            return ResponseEntity.ok(ApiResponse.success("获取电影详情成功", movie));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取电影详情失败：" + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieDTO>> createMovie(
            @Valid @RequestBody MovieDTO movieDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieDTO createdMovie = movieService.createMovie(movieDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("创建电影成功", createdMovie));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建电影失败：" + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDTO>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO movieDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieDTO updatedMovie = movieService.updateMovie(id, movieDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("更新电影成功", updatedMovie));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新电影失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMovie(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            movieService.deleteMovie(id, userId);
            return ResponseEntity.ok(ApiResponse.success("删除电影成功", "电影已删除"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除电影失败：" + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> searchMovies(
            @RequestParam String keyword,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            List<MovieDTO> movies = movieService.searchMovies(keyword, userId);
            return ResponseEntity.ok(ApiResponse.success("搜索电影成功", movies));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("搜索电影失败：" + e.getMessage()));
        }
    }

    @GetMapping("/viewable")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getViewableMovies(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieDTO> movies = movieService.getAllViewableMovies(userId);
            return ResponseEntity.ok(ApiResponse.success("获取可观看电影成功", movies));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取可观看电影失败：" + e.getMessage()));
        }
    }
}
