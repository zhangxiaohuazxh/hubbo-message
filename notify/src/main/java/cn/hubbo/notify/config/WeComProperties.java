package cn.hubbo.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 企业微信应用配置
 * <p>
 * 对应 application.yaml 中的 {@code wecom} 配置段：
 * <pre>
 * wecom:
 *   corp-id: wwxxxx
 *   corp-secret: xxxx
 *   agent-id: 1000002
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "wecom")
public class WeComProperties {

    /**
     * 企业ID（CorpId）
     */
    private String corpId;

    /**
     * 应用的凭证密钥（Secret）
     */
    private String corpSecret;

    /**
     * 默认企业应用id（AgentId），单条消息可在 {@code WeComNotifyMessage.agentId} 中覆盖
     */
    private Integer agentId;
}
