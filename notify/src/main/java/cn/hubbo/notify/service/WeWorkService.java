package cn.hubbo.notify.service;

import cn.hubbo.notify.config.WeComProperties;
import cn.hubbo.notify.enums.WeComMsgType;
import cn.hubbo.notify.utils.ProxyContext;
import cn.hubbo.notify.vo.WeComNotifyMessage;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMediaService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 企业微信消息通知服务
 * <p>
 * 将统一的 {@link WeComNotifyMessage} 映射为 SDK 的 {@link WxCpMessage} 并发送，
 * 供业务方按不同消息类型（文本、Markdown、图片、语音、视频、文件、文本卡片、图文等）通知到指定成员/部门/标签。
 */
@Slf4j
@Service
@SofaService
@RequiredArgsConstructor
public class WeWorkService {

	private final WxCpService wxCpService;

	private final WeComProperties weComProperties;

	private WxCpMediaService mediaService;

	@PostConstruct
	public void init() {
		mediaService = wxCpService.getMediaService();
	}

	/**
	 * 发送企业微信通知消息
	 *
	 * @param message 消息参数
	 * @throws WxErrorException 微信接口调用失败
	 */
	public void send(WeComNotifyMessage message) throws WxErrorException {
		WxCpMessage msg = convert(message);
		try {
			//ProxyContext.setProxy("47.98.100.134", 80);
			wxCpService.getMessageService().send(msg);
		} catch (Exception e) {
			log.error("企微消息发送失败", e);
		} finally {
			ProxyContext.clear();
		}
	}

	public String uploadMedia(String mediaType, InputStream inputStream, String filename) throws WxErrorException {
		WxMediaUploadResult result = mediaService.upload(mediaType, inputStream, filename);
		return result.getMediaId();
	}

	/**
	 * 将消息参数 VO 映射为 SDK 的 WxCpMessage，并进行轻量参数校验
	 */
	private WxCpMessage convert(WeComNotifyMessage vo) {
		WeComMsgType type = vo.getMsgType();
		if (type == null) {
			throw new IllegalArgumentException("msgType 不能为空");
		}
		Integer agentId = resolveAgentId(vo.getAgentId());
		if (agentId == null) {
			throw new IllegalArgumentException("agentId 不能为空：请在消息体 agentId 或配置 wecom.agent-id 中指定");
		}
		if (!StringUtils.hasText(vo.getToUser()) && !StringUtils.hasText(vo.getToParty()) && !StringUtils.hasText(vo.getToTag())) {
			throw new IllegalArgumentException("toUser/toParty/toTag 至少提供一个");
		}
		WxCpMessage msg = new WxCpMessage();
		msg.setMsgType(type.getCode());
		msg.setToUser(vo.getToUser());
		msg.setToParty(vo.getToParty());
		msg.setToTag(vo.getToTag());
		msg.setAgentId(agentId);
		if (vo.getSafe() != null) {
			msg.setSafe(String.valueOf(vo.getSafe()));
		}
		switch (type) {
			case TEXT, MARKDOWN -> msg.setContent(requireNotBlank(vo.getContent(), "content 不能为空"));
			case IMAGE, VOICE, FILE -> msg.setMediaId(requireNotBlank(vo.getMediaId(), "mediaId 不能为空"));
			case VIDEO -> {
				msg.setMediaId(requireNotBlank(vo.getMediaId(), "mediaId 不能为空"));
				msg.setThumbMediaId(vo.getThumbMediaId());
			}
			case TEXTCARD -> {
				msg.setTitle(requireNotBlank(vo.getTitle(), "title 不能为空"));
				msg.setUrl(requireNotBlank(vo.getUrl(), "url 不能为空"));
				msg.setDescription(vo.getDescription());
				msg.setBtnTxt(vo.getBtnTxt());
			}
			case NEWS -> {
				if (vo.getArticles() == null || vo.getArticles().isEmpty()) {
					throw new IllegalArgumentException("news 类型必须提供 articles");
				}
				msg.setArticles(vo.getArticles());
			}
			case MPNEWS -> {
				if (vo.getMpnewsArticles() == null || vo.getMpnewsArticles().isEmpty()) {
					throw new IllegalArgumentException("mpnews 类型必须提供 mpnewsArticles");
				}
				msg.setMpnewsArticles(vo.getMpnewsArticles());
			}
		}
		return msg;
	}

	/**
	 * agentId 解析：单条消息覆盖优先，否则回落到全局配置
	 */
	private Integer resolveAgentId(Integer override) {
		return override != null ? override : weComProperties.getAgentId();
	}

	private String requireNotBlank(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw new IllegalArgumentException(message);
		}
		return value;
	}
}
