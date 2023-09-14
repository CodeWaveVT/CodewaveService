package edu.vt.codewaveservice.manager;

import com.alibaba.excel.util.StringUtils;
import edu.vt.codewaveservice.utils.XunFeiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Slf4j
@Service
public class AiXunFeiManager {

    public String TextToAudio(String text,String fileName) throws IOException {
        if (!StringUtils.isNotBlank(text)) {
            return "empty text";
        }
        //过滤图片,h5标签
        text = text.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "").trim();
        //调用微服务接口获取音频base64
        String result = "";
        try {
            result = XunFeiUtil.convertText(text);
        } catch (Exception e) {
            log.error("【文字转语音接口调用异常】", e);
        }
        //音频数据
        String OUTPUT_FILE_PATH = "src/main/resources/tts/" +fileName+System.currentTimeMillis() + ".mp3";

        byte[] audioByte = Base64.getDecoder().decode(result);
        OutputStream outputStream = new FileOutputStream(OUTPUT_FILE_PATH);
        outputStream.write(audioByte);
        outputStream.flush();

        return "audioByte";
    }

}
