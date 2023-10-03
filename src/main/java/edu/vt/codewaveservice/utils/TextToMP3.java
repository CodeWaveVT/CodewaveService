package edu.vt.codewaveservice.utils;


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import static edu.vt.codewaveservice.utils.SystemConstants.WORDS_PER_FILE;

public class TextToMP3 {

    private TempFileManager tempFileManager;

    private S3Utils s3Utils;

    public TextToMP3() {
        tempFileManager = new TempFileManager();
        s3Utils = new S3Utils();
    }

    public static void main(String[] args) throws IOException {
        TextToMP3 textToMP3 = new TextToMP3();
        String inputFilePath = "src/main/resources/text/test_book.txt";
        String outputFilePath = textToMP3.generateMultiPartMp3(inputFilePath);
        //textToMP3.textToAudio("hello world", "test.mp3");
//        String s3Url = textToMP3.uploadAndCleanUp(outputFilePath);  // 上传文件并清除临时文件
//        System.out.println("Uploaded MP3 URL: " + s3Url);
    }

    public String uploadAndCleanUp(String outputFilePath) {
        String fileName = new File(outputFilePath).getName();
        String s3Url = s3Utils.uploadFile(outputFilePath, fileName);  // 上传文件到S3
        tempFileManager.deleteAllTempFiles();  // 清除所有临时文件
        return s3Url;
    }

    public void textToAudio(String text, String taskName) throws IOException {
        String path = SystemConstants.TTS_PATH + taskName;
        String result = "";
        try {
            text = text.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "").trim();
            result = XunFeiUtil.convertText(text);  // Assuming XunFeiUtil is accessible
        } catch (Exception e) {
            e.printStackTrace();  // Replacing log.error for simplicity
        }
        byte[] audioByte = Base64.getDecoder().decode(result);
        try (OutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(audioByte);
        }
    }

    public String generateMultiPartMp3(String inputFilePath) {
        List<String> subTexts = splitTextFile(inputFilePath);

        List<File> mp3Files = new ArrayList<>();
        for (int i = 0; i < subTexts.size(); i++) {
            String subText = subTexts.get(i);
            System.out.println("Subtext " + i + ": " + subText);
            String taskName = String.format("part%s.mp3", i);
            try {
                textToAudio(subText, taskName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String path = SystemConstants.TTS_PATH + taskName;
            mp3Files.add(tempFileManager.createTempFile(path));  // Creating temp files using TempFileManager
        }

        String outputFileName = new File(inputFilePath).getName().replace(".txt", ".mp3");
        String outputFilePath = SystemConstants.TTS_PATH + outputFileName;
        mergeMP3Files(mp3Files, outputFilePath);
        //tempFileManager.createTempFile(outputFilePath);  // Registering the final output file as a temp file
        tempFileManager.deleteAllTempFiles();
        return outputFilePath;
    }


    public List<String> splitTextFile(String filePath) {
        List<String> subTexts = new ArrayList<>();
        try {
            String content = Files.asCharSource(new File(filePath), Charsets.UTF_8).read();
            Iterable<String> words = Splitter.onPattern("\\s+").split(content);
            Iterator<String> iterator = words.iterator();

            StringBuilder sb = new StringBuilder();
            int wordCount = 0;
            while (iterator.hasNext()) {
                sb.append(iterator.next()).append(" ");
                wordCount++;
                if (wordCount >= WORDS_PER_FILE) {
                    subTexts.add(sb.toString().trim());
                    sb = new StringBuilder();
                    wordCount = 0;
                }
            }
            if (sb.length() > 0) {
                subTexts.add(sb.toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subTexts;
    }

    public void mergeMP3Files(List<File> mp3Files, String outputFilePath) {
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            for (File mp3File : mp3Files) {
                try (FileInputStream fis = new FileInputStream(mp3File)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
