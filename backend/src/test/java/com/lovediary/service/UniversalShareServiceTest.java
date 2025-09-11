package com.lovediary.service;

import com.lovediary.entity.UniversalShareLink;
import com.lovediary.repository.UniversalShareLinkRepository;
import com.lovediary.service.impl.UniversalShareServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniversalShareServiceTest {

    @Mock
    private UniversalShareLinkRepository universalShareLinkRepository;

    @InjectMocks
    private UniversalShareServiceImpl universalShareService;

    @Test
    void testCreateShareLink() {
        // 准备测试数据
        Long targetId = 1L;
        UniversalShareLink.ShareType shareType = UniversalShareLink.ShareType.DIARY;
        Long userId = 1L;

        UniversalShareLink expectedLink = new UniversalShareLink();
        expectedLink.setId(1L);
        expectedLink.setTargetId(targetId);
        expectedLink.setShareType(shareType);
        expectedLink.setShareToken("test-token");
        expectedLink.setIsActive(true);

        when(universalShareLinkRepository.save(any(UniversalShareLink.class)))
                .thenReturn(expectedLink);

        // 执行测试
        UniversalShareLink result = universalShareService.createShareLink(targetId, shareType, userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(targetId, result.getTargetId());
        assertEquals(shareType, result.getShareType());
        assertTrue(result.getIsActive());
        assertNotNull(result.getShareToken());
    }

    @Test
    void testShareTypeEnum() {
        // 测试枚举值
        assertEquals("日记", UniversalShareLink.ShareType.DIARY.getDescription());
        assertEquals("信件", UniversalShareLink.ShareType.LETTER.getDescription());
        assertEquals("电影", UniversalShareLink.ShareType.MOVIE.getDescription());
        assertEquals("聊天记录", UniversalShareLink.ShareType.CHAT_RECORD.getDescription());
        assertEquals("音乐", UniversalShareLink.ShareType.MUSIC.getDescription());
    }
}
