package cn.hubbo.notify.web;

import cn.hubbo.notify.entity.Result;
import cn.hubbo.notify.service.WeWorkService;
import cn.hubbo.notify.utils.FileUtils;
import cn.hubbo.notify.vo.WeComNotifyMessage;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpMediaService;
import me.chanjar.weixin.cp.api.WxCpOaWeDriveService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.oa.wedrive.WxCpFileList;
import me.chanjar.weixin.cp.bean.oa.wedrive.WxCpFileListRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Tag(name = "企微API")
@Slf4j
@RestController
@RequestMapping("/wework")
@RequiredArgsConstructor
public class WeWorkController {

    private final WeWorkService weWorkService;

    private final WxCpService wxCpService;

    private WxCpMediaService mediaService;

    @PostConstruct
    public void init() {
        mediaService = wxCpService.getMediaService();
    }


    @Operation(summary = "发送企微消息")
    @PostMapping("/msg/send")
    public Result<?> sendWeWorkMessage(@RequestBody WeComNotifyMessage message) throws WxErrorException {
        weWorkService.send(message);
        return Result.success();
    }

    @Operation(summary = "消息素材上传")
    @Parameters({
            @Parameter(name = "file", description = "上传的文件")
    })
    @PostMapping("/media/upload")
    public Result<?> uploadFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        // 3QFip3x-MSg8mSVG6eTLmRiozi7dqsxFv9sZ3kFlIfjAmZLxwPE77ZcIU07CmtC-_XjAFGpXX0N02XSziU7NPMw
        String mediaId = weWorkService.uploadMedia(FileUtils.mapMimeTypeToMediaType(FileUtils.mimeType(file.getInputStream()), filename), file.getInputStream(), file.getOriginalFilename());
        return Result.success(mediaId);
    }


}
