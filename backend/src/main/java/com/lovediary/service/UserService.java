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
            return jwtUtil.generateToken(username, user.getRole().name());
        }
        return null;
    }

    @Transactional
    public void initializeDefaultUser() {
        // 只在不存在时才创建 admin 用户
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setDisplayName("管理员");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            
            System.out.println("Admin 用户已创建: admin/admin");
        } else {
            System.out.println("Admin 用户已存在");
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