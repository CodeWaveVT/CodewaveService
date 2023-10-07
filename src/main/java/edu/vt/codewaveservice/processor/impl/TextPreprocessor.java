package edu.vt.codewaveservice.processor.impl;

import edu.vt.codewaveservice.processor.ProcessingContext;
import edu.vt.codewaveservice.processor.Processor;

public class TextPreprocessor implements Processor {
    @Override
    public void process(ProcessingContext context) {
        String text = context.getText().replaceAll("\\&[a-zA-Z]{1,10};", "")
                .replaceAll("<[^>]*>", "")
                .replaceAll("[(/>)<]", "")
                .trim();
        context.setText(text);
    }
}
