const axios = require('axios');

const BASE_URL = 'http://localhost:8080';

// 测试用户信息编辑API
async function testUserProfileAPI() {
  try {
    console.log('🧪 开始测试用户信息编辑API...\n');
    
    // 1. 测试登录获取token
    console.log('1. 测试登录...');
    const loginResponse = await axios.post(`${BASE_URL}/auth/login`, {
      username: 'admin',
      password: 'admin'
    });
    
    if (loginResponse.data.success) {
      const token = loginResponse.data.data.token;
      console.log('✅ 登录成功，获取到token');
      
      // 2. 测试获取用户信息
      console.log('\n2. 测试获取用户信息...');
      const profileResponse = await axios.get(`${BASE_URL}/auth/profile`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (profileResponse.data.success) {
        console.log('✅ 获取用户信息成功');
        console.log('用户信息:', profileResponse.data.data);
        
        // 3. 测试更新用户信息
        console.log('\n3. 测试更新用户信息...');
        const updateData = {
          displayName: '测试管理员',
          password: 'newpassword123',
          confirmPassword: 'newpassword123'
        };
        
        const updateResponse = await axios.put(`${BASE_URL}/auth/profile`, updateData, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (updateResponse.data.success) {
          console.log('✅ 更新用户信息成功');
          console.log('更新结果:', updateResponse.data);
          
          // 4. 验证更新结果
          console.log('\n4. 验证更新结果...');
          const verifyResponse = await axios.get(`${BASE_URL}/auth/profile`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          
          if (verifyResponse.data.success) {
            console.log('✅ 验证更新结果成功');
            console.log('更新后的用户信息:', verifyResponse.data.data);
          } else {
            console.log('❌ 验证更新结果失败:', verifyResponse.data.message);
          }
        } else {
          console.log('❌ 更新用户信息失败:', updateResponse.data.message);
        }
      } else {
        console.log('❌ 获取用户信息失败:', profileResponse.data.message);
      }
    } else {
      console.log('❌ 登录失败:', loginResponse.data.message);
    }
    
  } catch (error) {
    console.error('❌ 测试过程中发生错误:', error.message);
    if (error.response) {
      console.error('错误详情:', error.response.data);
    }
  }
}

// 运行测试
testUserProfileAPI();
