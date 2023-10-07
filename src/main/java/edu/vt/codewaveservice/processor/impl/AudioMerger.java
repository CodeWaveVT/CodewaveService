package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.utils.SystemConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AudioMerger implements Processor {
    @Override
    public void process(ProcessingContext context) {
        String outputFileName = context.getFileName().replace(".txt", ".mp3");
        String outputFilePath = SystemConstants.TTS_PATH + outputFileName;

        mergeMP3Files(context.getMp3Files(), outputFilePath);
        context.setFinalMp3Path(outputFilePath);
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

