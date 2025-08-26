package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.MoviePlaybackDTO;
import com.lovediary.dto.MovieRoomDTO;
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
    public ResponseEntity<ApiResponse<MovieRoom>> createRoom(
            @Valid @RequestBody MovieRoomDTO roomDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.createRoom(roomDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("创建房间成功", room));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建房间失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<ApiResponse<MovieRoom>> joinRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.joinRoom(roomCode, userId);
            return ResponseEntity.ok(ApiResponse.success("加入房间成功", room));
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
    public ResponseEntity<ApiResponse<MovieRoom>> getRoom(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.getRoomByCode(roomCode);
            return ResponseEntity.ok(ApiResponse.success("获取房间信息成功", room));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取房间信息失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{roomCode}/members")
    public ResponseEntity<ApiResponse<List<MovieRoomMember>>> getRoomMembers(
            @PathVariable String roomCode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieRoomMember> members = movieRoomService.getRoomMembers(roomCode);
            return ResponseEntity.ok(ApiResponse.success("获取房间成员成功", members));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取房间成员失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{roomCode}/playback")
    public ResponseEntity<ApiResponse<MovieRoom>> updatePlayback(
            @PathVariable String roomCode,
            @Valid @RequestBody MoviePlaybackDTO playbackDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            MovieRoom room = movieRoomService.updatePlayback(roomCode, playbackDTO, userId);
            return ResponseEntity.ok(ApiResponse.success("更新播放状态成功", room));
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
    public ResponseEntity<ApiResponse<List<MovieRoom>>> getMyRooms(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long userId = getCurrentUserId(token);
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("用户未登录"));
            }
            List<MovieRoom> rooms = movieRoomService.getUserRooms(userId);
            return ResponseEntity.ok(ApiResponse.success("获取我的房间成功", rooms));
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
}

