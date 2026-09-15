package cn.hubbo.notify.config;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.util.http.apache.DefaultApacheHttpClientBuilder;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 企业微信 WxCpService 装配配置
 * <p>
 * 手动创建 {@link WxCpConfigStorage}（默认内存缓存）与 {@link WxCpService} Bean，
 * 供消息通知等服务注入使用。
 */
@Configuration
@EnableConfigurationProperties(WeComProperties.class)
@RequiredArgsConstructor
public class WeComConfiguration {

    private final WeComProperties properties;

    /**
     * 企业微信配置存储（内存版，单实例够用；多实例部署可替换为 Redis 实现）
     */
    @Bean
    public WxCpConfigStorage wxCpConfigStorage() {
        WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
        config.setCorpId(properties.getCorpId());
        config.setCorpSecret(properties.getCorpSecret());
        config.setAgentId(properties.getAgentId());
        config.setApacheHttpClientBuilder(DefaultApacheHttpClientBuilder.get());
        return config;
    }

    /**
     * 企业微信服务（统一入口，可获取消息、素材、部门等子服务）
     */
    @Bean
    public WxCpService wxCpService(WxCpConfigStorage wxCpConfigStorage) {
        WxCpServiceImpl service = new WxCpServiceImpl();
        service.setWxCpConfigStorage(wxCpConfigStorage);
        return service;
    }
}
