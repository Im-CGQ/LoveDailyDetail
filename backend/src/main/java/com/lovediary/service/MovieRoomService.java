package com.lovediary.service;

import com.lovediary.dto.MoviePlaybackDTO;
import com.lovediary.dto.MovieRoomDTO;
import com.lovediary.entity.MovieRoom;
import com.lovediary.entity.MovieRoomMember;

import java.util.List;

public interface MovieRoomService {

    // 创建房间
    MovieRoom createRoom(MovieRoomDTO roomDTO, Long userId);
    
    // 加入房间
    MovieRoom joinRoom(String roomCode, Long userId);
    
    // 离开房间
    void leaveRoom(String roomCode, Long userId);
    
    // 获取房间信息
    MovieRoom getRoomByCode(String roomCode);
    
    // 获取房间成员
    List<MovieRoomMember> getRoomMembers(String roomCode);
    
    // 更新播放状态
    MovieRoom updatePlayback(String roomCode, MoviePlaybackDTO playbackDTO, Long userId);
    
    // 获取播放状态
    MoviePlaybackDTO getPlaybackStatus(String roomCode);
    
    // 检查用户是否在房间中
    boolean isUserInRoom(String roomCode, Long userId);
    
    // 生成房间代码
    String generateRoomCode();
    
    // 获取用户创建的房间
    List<MovieRoom> getUserRooms(Long userId);
    
    // 删除房间
    void deleteRoom(String roomCode, Long userId);
}


