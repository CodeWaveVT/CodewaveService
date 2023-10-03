package edu.vt.codewaveservice.manager;

import com.alibaba.excel.util.StringUtils;
import edu.vt.codewaveservice.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;

@Slf4j
@Service
public class AiXunFeiManager {

    public String TextToAudioMultiPart(String text,String bookName) throws IOException {
        if (!StringUtils.isNotBlank(text)) {
            return "empty text";
        }

        String fileName = bookName+ TaskIdUtil.generateTaskID()+ ".txt";

        text = text.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "").trim();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SystemConstants.TEXT_PATH+fileName))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextToMP3 textToMP3 = new TextToMP3();
        String outputFilePath = textToMP3.generateMultiPartMp3(SystemConstants.TEXT_PATH+fileName);
        String s3Url = textToMP3.uploadAndCleanUp(outputFilePath);
        return s3Url;
    }

    private String uploadToS3(String localFilePath, String s3FileName) {
        S3Utils s3Utils = new S3Utils();
        return s3Utils.uploadFile(localFilePath, s3FileName);
    }

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
