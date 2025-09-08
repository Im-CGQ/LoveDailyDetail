# Views 目录结构说明

## 目录组织

views目录已按功能模块重新组织，便于维护和扩展：

```
views/
├── auth/                    # 认证相关
│   ├── Login.vue           # 登录页面
│   ├── Register.vue        # 注册页面
│   ├── EmailLogin.vue      # 邮箱登录
│   ├── EmailRegister.vue   # 邮箱注册
│   ├── PhoneLogin.vue      # 手机登录
│   ├── PhoneRegister.vue   # 手机注册
│   ├── LoginChoice.vue     # 登录方式选择
│   └── index.js            # 模块导出文件
├── diary/                   # 日记相关
│   ├── DiaryList.vue       # 日记列表
│   ├── Detail.vue          # 日记详情
│   ├── SharedDiary.vue     # 分享的日记
│   └── index.js
├── letter/                  # 信件相关
│   ├── LetterBox.vue       # 信箱
│   ├── LetterDetail.vue    # 信件详情
│   ├── EditLetter.vue      # 编辑信件
│   ├── SharedLetter.vue    # 分享的信件
│   └── index.js
├── movie/                   # 电影相关
│   ├── MovieList.vue       # 电影列表
│   ├── MovieDetail.vue     # 电影详情
│   ├── CreateMovie.vue     # 创建电影
│   ├── EditMovie.vue       # 编辑电影
│   ├── JoinRoom.vue        # 加入房间
│   ├── MovieRoom.vue       # 电影房间
│   └── index.js
├── chat/                    # 聊天相关
│   ├── ChatRecord.vue      # 聊天记录
│   └── index.js
├── calendar/                # 日历和纪念日
│   ├── Calendar.vue        # 日历
│   ├── AnniversaryList.vue # 纪念日列表
│   └── index.js
├── profile/                 # 用户资料
│   ├── EditProfile.vue     # 编辑资料
│   └── index.js
├── special/                 # 特殊功能页面
│   ├── Love.vue            # 爱的告白
│   ├── LoveTree.vue        # 樱花树
│   ├── LoveTreeDemo.vue    # 樱花树演示
│   └── index.js
├── common/                  # 通用页面
│   ├── Home.vue            # 首页
│   ├── Welcome.vue         # 欢迎页
│   └── index.js
├── admin/                   # 管理后台（保持现有结构）
│   ├── AdminHome.vue
│   ├── AdminLayout.vue
│   └── ...
├── index.js                 # 统一导出文件
└── README.md               # 本说明文件
```

## 使用方式

### 1. 直接导入单个组件
```javascript
import Login from '@/views/auth/Login.vue'
import DiaryList from '@/views/diary/DiaryList.vue'
```

### 2. 从模块导入
```javascript
import { Login, Register } from '@/views/auth'
import { DiaryList, Detail } from '@/views/diary'
```

### 3. 统一导入
```javascript
import { Login, DiaryList, MovieList } from '@/views'
```

## 优势

1. **模块化组织**：按功能模块分类，便于定位和维护
2. **清晰的职责分离**：每个目录负责特定的业务功能
3. **便于扩展**：新增功能时只需在对应模块下添加文件
4. **统一的导入方式**：通过index.js文件提供多种导入方式
5. **团队协作友好**：不同开发者可以专注于不同的模块

## 路由配置

路由配置已相应更新，所有路径都指向新的文件位置。如需添加新路由，请确保路径与新的目录结构一致。

## 注意事项

- 新增组件时，请将其放在对应的功能模块目录下
- 记得更新对应模块的index.js文件
- 保持目录结构的清晰和一致性
- 避免跨模块的强耦合
