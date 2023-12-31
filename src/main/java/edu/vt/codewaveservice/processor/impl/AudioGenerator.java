package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.common.SpringContext;
import edu.vt.codewaveservice.manager.TTSModels.TTSModel;
import edu.vt.codewaveservice.manager.TTSModels.TTSModelFactory;
import edu.vt.codewaveservice.processor.CriticalProcessor;
import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
@CriticalProcessor(retryCount = 2)
@Slf4j
public class AudioGenerator implements Processor {

    private TTSModelFactory ttsModelFactory;

    public AudioGenerator() {
        this.ttsModelFactory = SpringContext.getBean(TTSModelFactory.class);
    }

    @Override
    public void process(ProcessingContext context) {
        List<File> mp3Files = new ArrayList<>();
        List<String> subTexts = context.getSubTexts();
        String baseName = extractPart(context.getFileName());  // Assuming the extractPart method is accessible

        for (int i = 0; i < subTexts.size(); i++) {
            String subText = subTexts.get(i);
            String taskName = baseName + String.format("part%s.mp3", i);

            try {
                textToAudio(subText, taskName,context);
                String path = SystemConstants.TTS_PATH + taskName;
                mp3Files.add(context.getTempFileManager().createTempFile(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        context.setMp3Files(mp3Files);
    }

    private static String extractPart(String filePath) {
        int startIdx = filePath.lastIndexOf("/") + 1;
        int endIdx = filePath.lastIndexOf(".");
        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            return filePath.substring(startIdx, endIdx);
        } else {
            throw new IllegalArgumentException("Invalid file path format");
        }
    }

    public void textToAudio(String text, String taskName,ProcessingContext context) throws IOException {
        String path = SystemConstants.TTS_PATH + taskName;
        String result = "";
        TTSModel ttsModel = ttsModelFactory.getModel(context.getModelType());
        //System.out.printf("ttsModel: %s\n", ttsModel);
        try {
           // result = XunFeiUtil.convertText(text);
           // result = OpenaiUtil.textToSpeech(text);
            result = ttsModel.generateAudio(text);
        } catch (Exception e) {
            log.debug("IO exception occurred while calling AI service", e);
        }
        byte[] audioByte = Base64.getDecoder().decode(result);
        try (OutputStream outputStream = new FileOutputStream(path)) {
            outputStream.write(audioByte);
        }
    }
}

