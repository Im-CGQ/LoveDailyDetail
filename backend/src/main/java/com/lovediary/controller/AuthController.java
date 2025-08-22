package com.lovediary.controller;

import com.lovediary.dto.ApiResponse;
import com.lovediary.dto.LoginRequest;
import com.lovediary.dto.LoginResponse;
import com.lovediary.dto.RegisterRequest;
import com.lovediary.entity.User;
import com.lovediary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            if (userService.validateUser(request.getUsername(), request.getPassword())) {
                String token = userService.generateToken(request.getUsername());
                if (token != null) {
                    User user = userService.findByUsername(request.getUsername()).orElse(null);
                    
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(true);
                    response.setMessage("登录成功");
                    response.setToken(token);
                    
                    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
                    userInfo.setUsername(user.getUsername());
                    userInfo.setRole(user.getRole().name());
                    userInfo.setDisplayName(user.getDisplayName());
                    response.setUser(userInfo);
                    
                    return ResponseEntity.ok(ApiResponse.success("登录成功", response));
                }
            }
            return ResponseEntity.status(401).body(ApiResponse.error("用户名或密码错误"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("登录失败：" + e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkAuth(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                // 这里可以添加token验证逻辑
                return ResponseEntity.ok(ApiResponse.success("Token有效"));
            }
            return ResponseEntity.ok(ApiResponse.error("Token无效"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("验证失败：" + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 验证密码确认
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.status(400).body(ApiResponse.error("两次输入的密码不一致"));
            }
            
            // 检查用户名是否已存在
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(409).body(ApiResponse.error("用户名已存在"));
            }
            
            // 注册用户
            boolean success = userService.registerUser(
                request.getUsername(), 
                request.getPassword(), 
                request.getDisplayName()
            );
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("注册成功，请登录"));
            } else {
                return ResponseEntity.status(500).body(ApiResponse.error("注册失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("注册失败：" + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponse.error("未提供有效的认证令牌"));
            }
            
            String actualToken = token.substring(7);
            String username = userService.getUsernameFromToken(actualToken);
            if (username == null) {
                return ResponseEntity.ok(ApiResponse.error("无效的认证令牌"));
            }
            
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.error("用户不存在"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("获取用户信息失败：" + e.getMessage()));
        }
    }
} 