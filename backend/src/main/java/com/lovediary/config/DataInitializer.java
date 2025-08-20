package com.lovediary.config;

import com.lovediary.service.DiaryService;
import com.lovediary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    // @Autowired
    // private DiaryService diaryService;

    @Override
    public void run(String... args) throws Exception {
        // 初始化默认用户
        userService.initializeDefaultUser();
        
        // 暂时注释掉日记数据初始化，避免依赖问题
        // diaryService.initializeSampleData();
        
        System.out.println("用户初始化完成！");
    }
} 