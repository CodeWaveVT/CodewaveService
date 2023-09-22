package edu.vt.codewaveservice.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Slf4j
public class t2a {
    public static final String OUTPUT_FILE_PATH = "src/main/resources/tts/" + System.currentTimeMillis() + ".mp3";
    public static String OUTPUT_PATH = "src/main/resources/tts/";
    public void textToAudio(String text,String taskName) throws IOException {
        String path = t2a.OUTPUT_PATH+taskName;
        String result = "";
        try {
            result = XunFeiUtil.convertText(text);
        } catch (Exception e) {
            log.error("【文字转语音接口调用异常】", e);
        }
        //音频数据
        byte[] audioByte = Base64.getDecoder().decode(result);
        OutputStream outputStream = new FileOutputStream(path);
        outputStream.write(audioByte);
        outputStream.flush();
    }

}
