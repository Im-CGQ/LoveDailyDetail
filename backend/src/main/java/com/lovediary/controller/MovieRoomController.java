package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.MovieDTO;
import com.lovediary.dto.MoviePlaybackDTO;
import com.lovediary.dto.MovieRoomDTO;
import com.lovediary.dto.MovieRoomDetailDTO;
import com.lovediary.dto.MovieRoomMemberDTO;
import com.lovediary.entity.MovieRoom;
import com.lovediary.entity.MovieRoomMember;
import com.lovediary.entity.User;
import com.lovediary.service.MovieRoomService;
import com.lovediary.service.UserService;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movie-rooms")
@CrossOrigin(origins = "*")
public class MovieRoomController {

    @Autowired
    private MovieRoomService movieRoomService;
    
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

    @PostMapping
    public ResponseEntity<ApiResponse<MovieRoomDetailDTO>> createRoom(
            @Valid @RequestBody MovieRoomDTO roomDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.createRoom(roomDTO, userId);
            MovieRoomDetailDTO roomDetailDTO = convertToDetailDTO(room);
            return ResponseEntity.ok(ApiResponse.success("创建房间成功", roomDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建房间失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<ApiResponse<MovieRoomDetailDTO>> joinRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.joinRoom(roomCode, userId);
            MovieRoomDetailDTO roomDetailDTO = convertToDetailDTO(room);
            return ResponseEntity.ok(ApiResponse.success("加入房间成功", roomDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("加入房间失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{roomCode}/leave")
    public ResponseEntity<ApiResponse<String>> leaveRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            movieRoomService.leaveRoom(roomCode, userId);
            return ResponseEntity.ok(ApiResponse.success("离开房间成功", "已离开房间"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("离开房间失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<MovieRoomDetailDTO>> getRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.getRoomByCode(roomCode);
            MovieRoomDetailDTO roomDTO = convertToDetailDTO(room);
            return ResponseEntity.ok(ApiResponse.success("获取房间信息成功", roomDTO));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取房间信息失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{roomCode}/members")
    public ResponseEntity<ApiResponse<List<MovieRoomMemberDTO>>> getRoomMembers(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieRoomMember> members = movieRoomService.getRoomMembers(roomCode);
            List<MovieRoomMemberDTO> memberDTOs = members.stream()
                    .map(this::convertToMemberDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取房间成员成功", memberDTOs));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取房间成员失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{roomCode}/playback")
    public ResponseEntity<ApiResponse<MovieRoomDetailDTO>> updatePlayback(
            @PathVariable String roomCode,
            @Valid @RequestBody MoviePlaybackDTO playbackDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.updatePlayback(roomCode, playbackDTO, userId);
            MovieRoomDetailDTO roomDetailDTO = convertToDetailDTO(room);
            return ResponseEntity.ok(ApiResponse.success("更新播放状态成功", roomDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("更新播放状态失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{roomCode}/playback")
    public ResponseEntity<ApiResponse<MoviePlaybackDTO>> getPlaybackStatus(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MoviePlaybackDTO status = movieRoomService.getPlaybackStatus(roomCode);
            return ResponseEntity.ok(ApiResponse.success("获取播放状态成功", status));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取播放状态失败：" + e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MovieRoomDetailDTO>>> getMyRooms(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieRoom> rooms = movieRoomService.getUserRooms(userId);
            List<MovieRoomDetailDTO> roomDTOs = rooms.stream()
                    .map(this::convertToDetailDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("获取我的房间成功", roomDTOs));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取我的房间失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            movieRoomService.deleteRoom(roomCode, userId);
            return ResponseEntity.ok(ApiResponse.success("删除房间成功", "房间已删除"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("删除房间失败：" + e.getMessage()));
        }
    }

    private MovieRoomDetailDTO convertToDetailDTO(MovieRoom room) {
        MovieRoomDetailDTO dto = new MovieRoomDetailDTO();
        dto.setId(room.getId());
        dto.setRoomName(room.getRoomName());
        dto.setRoomCode(room.getRoomCode());
        dto.setPlayTime(room.getPlayTime());
        dto.setIsPlaying(room.getIsPlaying());
        dto.setLastUpdatedBy(room.getLastUpdatedBy());
        dto.setLastUpdatedAt(room.getLastUpdatedAt());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        
        // 设置创建者信息
        if (room.getCreator() != null) {
            dto.setCreatorId(room.getCreator().getId());
            dto.setCreatorName(room.getCreator().getDisplayName());
        }
        
        // 设置电影信息
        if (room.getMovie() != null) {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setId(room.getMovie().getId());
            movieDTO.setTitle(room.getMovie().getTitle());
            movieDTO.setDescription(room.getMovie().getDescription());
            movieDTO.setCoverUrl(room.getMovie().getCoverUrl());
            movieDTO.setMovieUrl(room.getMovie().getMovieUrl());
            movieDTO.setFileName(room.getMovie().getFileName());
            movieDTO.setIsPublic(room.getMovie().getIsPublic());
            movieDTO.setDurationMinutes(room.getMovie().getDurationMinutes());
            movieDTO.setFileSize(room.getMovie().getFileSize());
            movieDTO.setWidth(room.getMovie().getWidth());
            movieDTO.setHeight(room.getMovie().getHeight());
            movieDTO.setCreatedAt(room.getMovie().getCreatedAt());
            movieDTO.setUpdatedAt(room.getMovie().getUpdatedAt());
            
            // 设置创建者信息
            if (room.getMovie().getUser() != null) {
                movieDTO.setCreatorId(room.getMovie().getUser().getId());
                movieDTO.setCreatorName(room.getMovie().getUser().getDisplayName());
            }
            
            // 设置伴侣信息
            if (room.getMovie().getPartner() != null) {
                movieDTO.setPartnerId(room.getMovie().getPartner().getId());
                movieDTO.setPartnerName(room.getMovie().getPartner().getDisplayName());
            }
            
            dto.setMovie(movieDTO);
        }
        
        // 设置成员信息
        if (room.getMembers() != null) {
            List<MovieRoomMemberDTO> memberDTOs = room.getMembers().stream()
                    .map(this::convertToMemberDTO)
                    .collect(Collectors.toList());
            dto.setMembers(memberDTOs);
        }
        
        return dto;
    }

    private MovieRoomMemberDTO convertToMemberDTO(MovieRoomMember member) {
        MovieRoomMemberDTO dto = new MovieRoomMemberDTO();
        dto.setId(member.getId());
        dto.setJoinedAt(member.getJoinedAt());
        dto.setIsOnline(member.getIsOnline());
        
        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setUsername(member.getUser().getUsername());
            dto.setDisplayName(member.getUser().getDisplayName());
            
            // 设置用户对象
            MovieRoomMemberDTO.UserInfo userInfo = new MovieRoomMemberDTO.UserInfo();
            userInfo.setId(member.getUser().getId());
            userInfo.setUsername(member.getUser().getUsername());
            userInfo.setDisplayName(member.getUser().getDisplayName());
            dto.setUser(userInfo);
        }
        
        return dto;
    }
}

