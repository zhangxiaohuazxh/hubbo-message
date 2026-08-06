package cn.hubbo.notify.service;

import cn.hubbo.notify.config.WeComProperties;
import cn.hubbo.notify.enums.WeComMsgType;
import cn.hubbo.notify.vo.WeComNotifyMessage;
import me.chanjar.weixin.cp.api.WxCpMessageService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.article.NewArticle;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link WeComNotifyService} 映射与发送逻辑单元测试
 */
class WeComNotifyServiceTest {

    private final WxCpService wxCpService = mock(WxCpService.class);
    private final WxCpMessageService wxCpMessageService = mock(WxCpMessageService.class);
    private final WeComProperties properties = new WeComProperties();
    private final WeComNotifyService service = new WeComNotifyService(wxCpService, properties);

    @BeforeEach
    void setUp() {
        // 默认配置一个全局 agentId，使大部分用例聚焦于各自要验证的校验/映射分支
        properties.setAgentId(1000002);
        when(wxCpService.getMessageService()).thenReturn(wxCpMessageService);
    }

    @Test
    void sendTextWithAgentIdOverride_mapsAllFields() throws Exception {
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("@all");
        message.setMsgType(WeComMsgType.TEXT);
        message.setContent("hello");
        message.setSafe(1);
        message.setAgentId(100);

        service.send(message);

        WxCpMessage sent = captureSent();
        assertThat(sent.getMsgType()).isEqualTo("text");
        assertThat(sent.getToUser()).isEqualTo("@all");
        assertThat(sent.getContent()).isEqualTo("hello");
        assertThat(sent.getSafe()).isEqualTo("1");
        assertThat(sent.getAgentId()).isEqualTo(100);
    }

    @Test
    void sendAgentIdNull_fallsBackToGlobalConfig() throws Exception {
        properties.setAgentId(1000002);
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("zhangsan");
        message.setMsgType(WeComMsgType.TEXT);
        message.setContent("hello");

        service.send(message);

        assertThat(captureSent().getAgentId()).isEqualTo(1000002);
    }

    @Test
    void sendNews_setsArticles() throws Exception {
        NewArticle article = NewArticle.builder()
                .title("标题")
                .url("https://example.com")
                .build();
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("zhangsan");
        message.setMsgType(WeComMsgType.NEWS);
        message.setArticles(List.of(article));

        service.send(message);

        WxCpMessage sent = captureSent();
        assertThat(sent.getArticles()).hasSize(1);
        assertThat(sent.getArticles().get(0).getTitle()).isEqualTo("标题");
    }

    @Test
    void sendAgentIdUnset_throwsIllegalArgumentException() {
        properties.setAgentId(null);
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("zhangsan");
        message.setMsgType(WeComMsgType.TEXT);
        message.setContent("hello");

        assertThatThrownBy(() -> service.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("agentId");
    }

    @Test
    void sendMissingMsgType_throwsIllegalArgumentException() {
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("zhangsan");

        assertThatThrownBy(() -> service.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("msgType");
    }

    @Test
    void sendTextMissingContent_throwsIllegalArgumentException() {
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setToUser("zhangsan");
        message.setMsgType(WeComMsgType.TEXT);

        assertThatThrownBy(() -> service.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content");
    }

    @Test
    void sendNoRecipient_throwsIllegalArgumentException() {
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setMsgType(WeComMsgType.TEXT);
        message.setContent("hello");

        assertThatThrownBy(() -> service.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("toUser");
    }

    private WxCpMessage captureSent() throws Exception {
        ArgumentCaptor<WxCpMessage> captor = ArgumentCaptor.forClass(WxCpMessage.class);
        verify(wxCpMessageService).send(captor.capture());
        return captor.getValue();
    }
}
