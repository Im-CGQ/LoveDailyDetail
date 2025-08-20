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

    @Autowired
    private DiaryService diaryService;

    @Override
    public void run(String... args) throws Exception {
        // 初始化默认用户
        userService.initializeDefaultUser();
        
        // 初始化示例日记数据
        diaryService.initializeSampleData();
        
        System.out.println("数据初始化完成！");
    }
} 