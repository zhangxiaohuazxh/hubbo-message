package cn.hubbo.notify.utils;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static String mimeType(InputStream inputStream) throws IOException {
        return new Tika().detect(inputStream);
    }

    /**
     * 将 MIME 类型映射为企业微信支持的媒体类型常量
     *
     * @param mimeType 实际检测到的 MIME 类型，如 "image/jpeg"
     * @param fileName 原始文件名（用于辅助判断 thumb）
     */
    public static String mapMimeTypeToMediaType(String mimeType, String fileName) {
        if (mimeType == null) {
            return null;
        }
        // 1. 图片 -> IMAGE（除非文件名中包含 "thumb" 标记，可转为 THUMB）
        if (mimeType.startsWith("image/")) {
            if (fileName != null && fileName.toLowerCase().contains("thumb")) {
                return "thumb";
            }
            return "image";
        }
        // 2. 视频 -> VIDEO
        if (mimeType.startsWith("video/")) {
            return "video";
        }
        // 3. 音频 -> VOICE（企业微信支持 amr/wav 等）
        if (mimeType.startsWith("audio/") || "voice/amr".equals(mimeType) || "voice/wav".equals(mimeType)) {
            return "voice";
        }
        // 4. 其他所有类型（文档、压缩包等）-> FILE
        return "file";
    }


}
