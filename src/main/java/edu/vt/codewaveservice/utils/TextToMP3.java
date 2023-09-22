package edu.vt.codewaveservice.utils;


import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextToMP3 {
    @Resource
    private static XunFeiUtil xunFeiUtil;
    private static final int WORDS_PER_FILE = 50;
    private static final String INPUT_FILE_PATH = "src/main/resources/text/test_book.txt";
    private static final String OUTPUT_FILE_PATH = "src/main/resources/tts/final_output.mp3";
    public static String getName(String input) {
        if (input == null) {
            return "**********";
        }

        if (input.length() >= 10) {
            return input.substring(0, 10);
        } else {
            int numberOfAsterisks = 10 - input.length();
            StringBuilder result = new StringBuilder(input);
            for (int i = 0; i < numberOfAsterisks; i++) {
                result.append("*");
            }
            return result.toString();
        }
    }


    public static void generateMultiPartMp3(){
        List<String> subTexts = splitTextFile(INPUT_FILE_PATH);
        for (int i = 0; i < subTexts.size(); i++) {
            System.out.println(String.format("======text file part %s =======",i));
            System.out.println(subTexts.get(i));
        }
        List<File> mp3Files = new ArrayList<>();
        t2a t2a = new t2a();
        for (int i=0;i<subTexts.size();i++) {
            String subText = subTexts.get(i);
            String taskName = getName(subText)+String.format("part%s.mp3",i);
            try {
                t2a.textToAudio(subText,taskName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String path = t2a.OUTPUT_PATH+taskName;
            mp3Files.add(new File(path));
            // waitForFileToExist(path);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        mergeMP3Files(mp3Files, OUTPUT_FILE_PATH);
    }

    public static void main(String[] args) throws IOException {
        List<String> subTexts = splitTextFile(INPUT_FILE_PATH);
        for (int i = 0; i < subTexts.size(); i++) {
            System.out.println(String.format("======text file part %s =======",i));
            System.out.println(subTexts.get(i));
        }
        List<File> mp3Files = new ArrayList<>();
        t2a t2a = new t2a();
        for (int i=0;i<subTexts.size();i++) {
            String subText = subTexts.get(i);
            String taskName = getName(subText)+String.format("part%s.mp3",i);
            t2a.textToAudio(subText,taskName);
            String path = t2a.OUTPUT_PATH+taskName;
            mp3Files.add(new File(path));
           // waitForFileToExist(path);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        mergeMP3Files(mp3Files, OUTPUT_FILE_PATH);
    }

    private static void waitForFileToExist(String filePath) {
        File file = new File(filePath);
        int maxAttempts = 12; // 尝试12次，总等待时间为60秒
        int attempts = 0;

        while (!file.exists() && attempts < maxAttempts) {
            try {
                Thread.sleep(5000); // 等待5秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempts++;
        }

        if (attempts == maxAttempts) {
            // 文件未在预期时间内生成，您可以在此处添加错误处理逻辑
            System.out.println("Error: AI generated MP3 file not found after waiting for 60 seconds.");
        }
    }

    public static List<String> splitTextFile(String filePath) {
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

    public static void mergeMP3Files(List<File> mp3Files, String outputFilePath) {
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
