package com.lovediary.service;

import com.lovediary.entity.User;
import com.lovediary.repository.UserRepository;
import com.lovediary.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean validateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public String generateToken(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return jwtUtil.generateToken(username, user.getRole().name(), user.getId());
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void initializeDefaultUser() {
        // 检查admin用户是否存在
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        
        if (existingAdmin.isPresent()) {
            // 如果存在，更新密码确保正确
            User admin = existingAdmin.get();
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setDisplayName("管理员");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin 用户密码已更新: admin/admin");
        } else {
            // 如果不存在，创建新的admin用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setDisplayName("管理员");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin 用户已创建: admin/admin");
        }
    }

    public boolean registerUser(String username, String password, String displayName) {
        if (userRepository.existsByUsername(username)) {
            return false;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayName(displayName);
        user.setRole(User.Role.USER);
        
        userRepository.save(user);
        return true;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
} 