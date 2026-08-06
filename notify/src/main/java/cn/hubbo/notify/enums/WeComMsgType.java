package cn.hubbo.notify.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.chanjar.weixin.common.api.WxConsts;

/**
 * 企业微信消息类型
 * <p>
 * 枚举值与 SDK 的 {@link WxConsts.KefuMsgType} 常量绑定；
 * {@link JsonValue} 使 HTTP 请求体直接使用企业微信原生小写 msgtype（如 "text"、"markdown"）。
 */
@Getter
@AllArgsConstructor
public enum WeComMsgType {

    TEXT(WxConsts.KefuMsgType.TEXT),
    MARKDOWN(WxConsts.KefuMsgType.MARKDOWN),
    IMAGE(WxConsts.KefuMsgType.IMAGE),
    VOICE(WxConsts.KefuMsgType.VOICE),
    VIDEO(WxConsts.KefuMsgType.VIDEO),
    FILE(WxConsts.KefuMsgType.FILE),
    TEXTCARD(WxConsts.KefuMsgType.TEXTCARD),
    NEWS(WxConsts.KefuMsgType.NEWS),
    MPNEWS(WxConsts.KefuMsgType.MPNEWS);

    @JsonValue
    private final String code;
}
