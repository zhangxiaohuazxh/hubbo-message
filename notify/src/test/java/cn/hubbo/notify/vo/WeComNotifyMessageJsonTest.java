package cn.hubbo.notify.vo;

import cn.hubbo.notify.enums.WeComMsgType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WeComNotifyMessage} 的 Jackson 序列化/反序列化契约测试：
 * 消息类型在 HTTP 请求体中应使用企业微信原生小写 msgtype。
 */
class WeComNotifyMessageJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deserializeLowercaseMsgType() throws Exception {
        String json = "{\"toUser\":\"@all\",\"msgType\":\"text\",\"content\":\"hi\"}";

        WeComNotifyMessage message = objectMapper.readValue(json, WeComNotifyMessage.class);

        assertThat(message.getMsgType()).isEqualTo(WeComMsgType.TEXT);
        assertThat(message.getContent()).isEqualTo("hi");
    }

    @Test
    void serializeMsgTypeUsesLowercaseCode() throws Exception {
        WeComNotifyMessage message = new WeComNotifyMessage();
        message.setMsgType(WeComMsgType.MARKDOWN);

        String json = objectMapper.writeValueAsString(message);

        assertThat(json).contains("\"msgType\":\"markdown\"");
    }
}
