# SharedDiary.vue 数据结构调整

## 问题描述

`SharedDiary.vue` 与 `Detail.vue` 界面相似，但数据结构处理不一致，导致图片、视频和背景音乐显示异常。

## 主要差异

### 1. 接口差异
- **SharedDiary.vue**: 使用 `getSharedDiary(shareToken)` 接口
- **Detail.vue**: 使用 `getDiaryById(diaryId)` 接口

### 2. 功能差异
- **SharedDiary.vue**: 分享页面，无需分享按钮和返回日历按钮
- **Detail.vue**: 详情页面，包含分享和返回功能

### 3. 数据结构差异
- **SharedDiary.vue**: 之前直接使用字符串URL
- **Detail.vue**: 使用对象结构（包含URL、宽高等信息）

## 调整内容

### 1. 图片数据结构调整

**修改前**:
```vue
<img 
  :src="image" 
  class="image" 
  @click="previewImage(index)"
  @load="onImageLoad"
/>
```

**修改后**:
```vue
<img 
  :src="image.imageUrl" 
  class="image" 
  :style="getImageStyle(image)"
  @click="previewImage(index)"
/>
```

### 2. 视频数据结构调整

**修改前**:
```vue
<video 
  :src="video"
  :poster="getVideoPoster(video)"
  class="video-player"
  preload="metadata"
  controls
  @ended="onVideoEnded"
  @play="onVideoPlay"
  @pause="onVideoPause"
  @loadstart="onVideoLoadStart"
  @loadeddata="onVideoLoadedData"
  @loadedmetadata="onVideoLoadedMetadata"
/>
```

**修改后**:
```vue
<video 
  :src="video.videoUrl"
  class="video-player"
  :style="getVideoStyle(video)"
  preload="metadata"
  :poster="generateVideoPoster(video.videoUrl, video)"
  controls
  @click="playVideo(index)"
>
  您的浏览器不支持视频播放
</video>
```

### 3. 背景音乐数据结构调整

**修改前**:
```javascript
const initAudio = () => {
  if (!diary.value?.backgroundMusic) return
  
  audioElement = new Audio(diary.value.backgroundMusic)
  audioElement.loop = true
}
```

**修改后**:
```javascript
const initAudio = () => {
  if (!diary.value?.backgroundMusic || diary.value.backgroundMusic.length === 0) return
  
  // 使用第一个背景音乐
  const music = diary.value.backgroundMusic[0]
  audioElement = new Audio(music.musicUrl)
  audioElement.loop = true
}
```

### 4. 新增方法

添加了与 `Detail.vue` 相同的方法：

1. **getImageStyle(image)**: 图片自适应样式
2. **getVideoStyle(video)**: 视频自适应样式
3. **generateVideoPoster(videoUrl, video)**: 生成视频封面URL

### 5. 简化方法

删除了不再需要的方法：
- `onVideoEnded`, `onVideoPlay`, `onVideoPause`
- `onVideoLoadStart`, `onVideoLoadedData`, `onVideoLoadedMetadata`
- `onImageLoad`
- `getVideoPoster` (替换为 `generateVideoPoster`)

## 数据结构说明

### SharedDiaryDTO 结构
```java
public class SharedDiaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private List<ImageInfoDTO> images;        // 图片数组
    private List<VideoInfoDTO> videos;        // 视频数组
    private List<DiaryBackgroundMusicDTO> backgroundMusic;  // 背景音乐数组
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userName;
    private String userDisplayName;
}
```

### ImageInfoDTO 结构
```java
public class ImageInfoDTO {
    private String imageUrl;    // 图片URL
    private String fileName;    // 文件名
    private Integer width;      // 宽度
    private Integer height;     // 高度
}
```

### VideoInfoDTO 结构
```java
public class VideoInfoDTO {
    private String videoUrl;    // 视频URL
    private String fileName;    // 文件名
    private Integer width;      // 宽度
    private Integer height;     // 高度
}
```

### DiaryBackgroundMusicDTO 结构
```java
public class DiaryBackgroundMusicDTO {
    private String musicUrl;    // 音乐URL
    private String fileName;    // 文件名
    private String title;       // 标题
    private String artist;      // 艺术家
    private Integer duration;   // 时长
}
```

## 功能保持

1. **图片预览**: 支持点击图片预览
2. **视频播放**: 支持视频播放和封面显示
3. **背景音乐**: 支持背景音乐播放控制
4. **打字机效果**: 保持描述文字的打字机效果
5. **响应式设计**: 保持自适应布局

## 测试建议

1. 通过分享链接访问 `SharedDiary.vue`
2. 验证图片是否正确显示和预览
3. 验证视频是否正确播放和显示封面
4. 验证背景音乐是否正确播放
5. 验证页面布局是否正常
