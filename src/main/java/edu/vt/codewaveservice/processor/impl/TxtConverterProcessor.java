package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class TxtConverterProcessor implements Processor {

    public void process(ProcessingContext context) {
        //MultipartFile file = context.getFile();
        byte[] txtData = context.getFile();
        if (txtData == null || txtData.length == 0) {
            log.debug("No TXT data available for processing");
            return;
        }
        try {
            String txtContent = readTxtContent(txtData);
            context.setText(txtContent);
        } catch (Exception e) {
            log.error("Error during TXT conversion", e);
            throw new RuntimeException(e);
        }
    }

    public String readTxtContent(MultipartFile file) throws IOException {

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public String readTxtContent(byte[] data) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
