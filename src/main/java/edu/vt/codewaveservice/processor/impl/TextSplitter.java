package edu.vt.codewaveservice.processor.impl;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;
import edu.vt.codewaveservice.utils.SystemConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static edu.vt.codewaveservice.utils.SystemConstants.WORDS_PER_FILE;

public class TextSplitter implements Processor {
    @Override
    public void process(ProcessingContext context) {
        String filePath = SystemConstants.TEXT_PATH + context.getFileName();
        List<String> subTexts = splitTextFile(filePath);
        context.setSubTexts(subTexts);
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
}
