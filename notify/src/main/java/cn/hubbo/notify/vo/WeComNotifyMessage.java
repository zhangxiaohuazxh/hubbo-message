package cn.hubbo.notify.vo;

import cn.hubbo.notify.enums.WeComMsgType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.cp.bean.article.MpnewsArticle;
import me.chanjar.weixin.cp.bean.article.NewArticle;

import java.util.List;

/**
 * 企业微信消息通知参数 VO
 * <p>
 * 统一封装发送目标、消息类型及各类型所需内容字段。
 * 各字段含义与企业微信「应用消息推送」接口一致，可参考
 * <a href="https://developer.work.weixin.qq.com/document/path/90236">消息推送文档</a>。
 */
@Data
@NoArgsConstructor
public class WeComNotifyMessage {


    @Schema(description = "接收消息的成员ID列表，多个用'|'分隔，最多1000个；传入'@all'则向应用全部成员发送",
            example = "zhangsan|lisi", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toUser;

    @Schema(description = "接收消息的部门ID列表，多个用'|'分隔，最多100个；当toUser为'@all'时忽略",
            example = "1|2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toParty;

    @Schema(description = "接收消息的标签ID列表，多个用'|'分隔，最多100个；当toUser为'@all'时忽略",
            example = "3|4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String toTag;

    @Schema(description = "消息类型（必填）", example = "text", requiredMode = Schema.RequiredMode.REQUIRED)
    private WeComMsgType msgType;

    @Schema(description = "消息正文，最长不超过2048字节（text/markdown类型使用）", example = "您好，欢迎使用！")
    private String content;

    @Schema(description = "媒体文件ID，通过上传临时素材接口获取（image/voice/video/file类型使用）",
            example = "1G6z5W9xK...")
    private String mediaId;

    @Schema(description = "视频消息缩略图的media_id（video类型使用）", example = "2H8y3Z4...")
    private String thumbMediaId;

    @Schema(description = "标题（video/textcard/news/mpnews类型使用）", example = "重要通知")
    private String title;

    @Schema(description = "描述（video/textcard类型使用）", example = "请点击链接查看详情")
    private String description;

    @Schema(description = "点击后跳转的链接，需包含http/https（textcard/news类型使用）",
            example = "https://example.com/detail")
    private String url;

    @Schema(description = "按钮文字，默认为'详情'，不超过4个汉字（textcard类型使用）", example = "查看", maxLength = 4)
    private String btnTxt;

    @Schema(description = "保密消息标识：0-可对外分享，1-不能分享且内容显示水印，2-仅限企业内部分享（mpnews支持2）",
            allowableValues = {"0", "1", "2"}, example = "0")
    private Integer safe;

    @Schema(description = "企业应用ID，覆盖全局配置；为空时使用 wecom.agent-id", example = "1000001")
    private Integer agentId;

    @Schema(description = "图文消息列表，支持1~8条（news类型使用）")
    private List<NewArticle> articles;

    @Schema(description = "图文消息列表，支持1~8条（mpnews类型使用）")
    private List<MpnewsArticle> mpnewsArticles;

}
