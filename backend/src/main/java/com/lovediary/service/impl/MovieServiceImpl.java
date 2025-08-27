package com.lovediary.service.impl;

import com.lovediary.dto.MovieDTO;
import com.lovediary.entity.Movie;
import com.lovediary.entity.User;
import com.lovediary.repository.MovieRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public List<MovieDTO> getAllViewableMovies(Long userId) {
        List<Movie> movies = movieRepository.findViewableMoviesByUserId(userId);
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDTO> getPublicMovies() {
        List<Movie> movies = movieRepository.findByIsPublicTrueOrderByCreatedAtDesc();
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDTO> getMyMovies(Long userId) {
        List<Movie> movies = movieRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDTO getMovieById(Long id, Long userId) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        
        if (!canViewMovie(id, userId)) {
            throw new RuntimeException("无权查看此电影");
        }
        
        return convertToDTO(movie);
    }

    @Override
    public MovieDTO createMovie(MovieDTO movieDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Movie movie = new Movie();
        BeanUtils.copyProperties(movieDTO, movie);
        movie.setUser(user);
        
        // 如果用户有伴侣，设置伴侣ID
        if (user.getPartner() != null) {
            movie.setPartner(user.getPartner());
        }
        
        // 确保宽高信息被正确设置
        if (movieDTO.getWidth() != null) {
            movie.setWidth(movieDTO.getWidth());
        }
        if (movieDTO.getHeight() != null) {
            movie.setHeight(movieDTO.getHeight());
        }
        
        // 确保时长信息被正确设置
        if (movieDTO.getDurationMinutes() != null) {
            movie.setDurationMinutes(movieDTO.getDurationMinutes());
        }
        
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    @Override
    public MovieDTO updateMovie(Long id, MovieDTO movieDTO, Long userId) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        
        if (!canEditMovie(id, userId)) {
            throw new RuntimeException("无权编辑此电影");
        }
        
        BeanUtils.copyProperties(movieDTO, movie, "id", "user", "partner", "createdAt");
        
        // 确保宽高信息被正确设置
        if (movieDTO.getWidth() != null) {
            movie.setWidth(movieDTO.getWidth());
        }
        if (movieDTO.getHeight() != null) {
            movie.setHeight(movieDTO.getHeight());
        }
        
        // 确保时长信息被正确设置
        if (movieDTO.getDurationMinutes() != null) {
            movie.setDurationMinutes(movieDTO.getDurationMinutes());
        }
        
        Movie updatedMovie = movieRepository.save(movie);
        return convertToDTO(updatedMovie);
    }

    @Override
    public void deleteMovie(Long id, Long userId) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        
        if (!canEditMovie(id, userId)) {
            throw new RuntimeException("无权删除此电影");
        }
        
        movieRepository.delete(movie);
    }

    @Override
    public List<MovieDTO> searchMovies(String keyword, Long userId) {
        List<Movie> movies = movieRepository.findViewableMoviesByUserIdAndTitleContaining(userId, keyword);
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MovieDTO> getMoviesByPage(Long userId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findViewableMoviesByUserId(userId, pageable);
        return movies.map(this::convertToDTO);
    }

    @Override
    public boolean canViewMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            return false;
        }
        
        // 如果是公开电影，任何人都可以查看
        if (movie.getIsPublic()) {
            return true;
        }
        
        // 如果是创建者，可以查看
        if (movie.getUser().getId().equals(userId)) {
            return true;
        }
        
        // 如果是伴侣，可以查看
        if (movie.getPartner() != null && movie.getPartner().getId().equals(userId)) {
            return true;
        }
        
        // 如果是创建者的伴侣，可以查看
        if (movie.getUser().getPartnerId() != null && movie.getUser().getPartnerId().equals(userId)) {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean canEditMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            return false;
        }
        
        // 只有创建者可以编辑
        return movie.getUser().getId().equals(userId);
    }

    private MovieDTO convertToDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        BeanUtils.copyProperties(movie, dto);
        
        // 设置创建者信息
        if (movie.getUser() != null) {
            dto.setCreatorId(movie.getUser().getId());
            dto.setCreatorName(movie.getUser().getDisplayName());
        }
        
        // 设置伴侣信息
        if (movie.getPartner() != null) {
            dto.setPartnerId(movie.getPartner().getId());
            dto.setPartnerName(movie.getPartner().getDisplayName());
        }
        
        // 确保宽高信息被正确设置
        dto.setWidth(movie.getWidth());
        dto.setHeight(movie.getHeight());
        
        // 确保时长信息被正确设置
        dto.setDurationMinutes(movie.getDurationMinutes());
        
        return dto;
    }
}
