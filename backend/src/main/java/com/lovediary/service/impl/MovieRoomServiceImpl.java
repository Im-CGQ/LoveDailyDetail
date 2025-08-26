package com.lovediary.service.impl;

import com.lovediary.dto.MoviePlaybackDTO;
import com.lovediary.dto.MovieRoomDTO;
import com.lovediary.entity.Movie;
import com.lovediary.entity.MovieRoom;
import com.lovediary.entity.MovieRoomMember;
import com.lovediary.entity.User;
import com.lovediary.repository.MovieRepository;
import com.lovediary.repository.MovieRoomMemberRepository;
import com.lovediary.repository.MovieRoomRepository;
import com.lovediary.repository.UserRepository;
import com.lovediary.service.MovieRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MovieRoomServiceImpl implements MovieRoomService {

    private final MovieRoomRepository movieRoomRepository;
    private final MovieRoomMemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MovieRoom createRoom(MovieRoomDTO roomDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Movie movie = movieRepository.findById(roomDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("电影不存在"));
        
        // 检查用户是否有权限观看此电影
        if (!canViewMovie(movie, userId)) {
            throw new RuntimeException("无权观看此电影");
        }
        
        MovieRoom room = new MovieRoom();
        room.setRoomName(roomDTO.getRoomName());
        room.setRoomCode(generateRoomCode());
        room.setMovie(movie);
        room.setCreator(user);
        room.setPlayTime(0.0);
        room.setIsPlaying(false);
        
        room = movieRoomRepository.save(room);
        
        // 创建者自动加入房间
        joinRoom(room.getRoomCode(), userId);
        
        return room;
    }

    @Override
    @Transactional
    public MovieRoom joinRoom(String roomCode, Long userId) {
        MovieRoom room = getRoomByCode(roomCode);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户是否已经在房间中
        if (isUserInRoom(roomCode, userId)) {
            return room;
        }
        
        // 检查用户是否有权限观看此电影
        if (!canViewMovie(room.getMovie(), userId)) {
            throw new RuntimeException("无权观看此电影");
        }
        
        MovieRoomMember member = new MovieRoomMember();
        member.setRoom(room);
        member.setUser(user);
        member.setIsOnline(true);
        member.setLastSeen(LocalDateTime.now());
        
        memberRepository.save(member);
        
        return room;
    }

    @Override
    @Transactional
    public void leaveRoom(String roomCode, Long userId) {
        memberRepository.deleteByRoomIdAndUserId(
                getRoomByCode(roomCode).getId(), 
                userId
        );
    }

    @Override
    public MovieRoom getRoomByCode(String roomCode) {
        return movieRoomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("房间不存在"));
    }

    @Override
    public List<MovieRoomMember> getRoomMembers(String roomCode) {
        MovieRoom room = getRoomByCode(roomCode);
        return memberRepository.findByRoomId(room.getId());
    }

    @Override
    @Transactional
    public MovieRoom updatePlayback(String roomCode, MoviePlaybackDTO playbackDTO, Long userId) {
        MovieRoom room = getRoomByCode(roomCode);
        
        // 检查用户是否在房间中
        if (!isUserInRoom(roomCode, userId)) {
            throw new RuntimeException("用户不在房间中");
        }
        
        room.setPlayTime(playbackDTO.getCurrentTime());
        room.setIsPlaying(playbackDTO.getIsPlaying());
        room.setLastUpdatedBy(userId);
        room.setLastUpdatedAt(LocalDateTime.now());
        
        return movieRoomRepository.save(room);
    }

    @Override
    public MoviePlaybackDTO getPlaybackStatus(String roomCode) {
        MovieRoom room = getRoomByCode(roomCode);
        
        MoviePlaybackDTO dto = new MoviePlaybackDTO();
        dto.setCurrentTime(room.getPlayTime());
        dto.setIsPlaying(room.getIsPlaying());
        
        return dto;
    }

    @Override
    public boolean isUserInRoom(String roomCode, Long userId) {
        MovieRoom room = getRoomByCode(roomCode);
        return memberRepository.existsByRoomIdAndUserId(room.getId(), userId);
    }

    @Override
    public String generateRoomCode() {
        Random random = new Random();
        String roomCode;
        do {
            roomCode = String.format("%06d", random.nextInt(1000000));
        } while (movieRoomRepository.existsByRoomCode(roomCode));
        
        return roomCode;
    }

    @Override
    public List<MovieRoom> getUserRooms(Long userId) {
        return movieRoomRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void deleteRoom(String roomCode, Long userId) {
        MovieRoom room = getRoomByCode(roomCode);
        
        // 只有房间创建者可以删除房间
        if (!room.getCreator().getId().equals(userId)) {
            throw new RuntimeException("只有房间创建者可以删除房间");
        }
        
        movieRoomRepository.delete(room);
    }

    private boolean canViewMovie(Movie movie, Long userId) {
        // 如果是公开电影，任何人都可以观看
        if (movie.getIsPublic()) {
            return true;
        }
        
        // 如果是用户自己创建的电影，可以观看
        if (movie.getUser().getId().equals(userId)) {
            return true;
        }
        
        // 如果是伴侣创建的电影，可以观看
        if (movie.getPartner() != null && movie.getPartner().getId().equals(userId)) {
            return true;
        }
        
        return false;
    }
}

