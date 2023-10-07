package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.utils.SystemConstants;
import edu.vt.codewaveservice.utils.TaskIdUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileWriter implements Processor {
    @Override
    public void process(ProcessingContext context) {
        String fileName = context.getBookName() + TaskIdUtil.generateTaskID() + ".txt";
        context.setFileName(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SystemConstants.TEXT_PATH + fileName))) {
            writer.write(context.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

