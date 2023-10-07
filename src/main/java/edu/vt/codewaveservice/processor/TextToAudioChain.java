package edu.vt.codewaveservice.processor;

import edu.vt.codewaveservice.processor.impl.*;

import java.util.ArrayList;
import java.util.List;

public class TextToAudioChain {
    private List<Processor> processors = new ArrayList<>();

    public TextToAudioChain() {
        addProcessor(new LoggingProcessorDecorator(new TextPreprocessor()));
        addProcessor(new LoggingProcessorDecorator(new TextFileWriter()));
        addProcessor(new LoggingProcessorDecorator(new TextSplitter()));
        addProcessor(new LoggingProcessorDecorator(new AudioGenerator()));
        addProcessor(new LoggingProcessorDecorator(new AudioMerger()));
        addProcessor(new LoggingProcessorDecorator(new S3Uploader()));
    }

    public String process(ProcessingContext context) throws Exception {
//        String fileType = new ConverterProcessorFactory().getFileExtension(context.getFile().getOriginalFilename());
//        System.out.println(fileType);
        String fileType = context.getFileType();
        Processor startingProcessor = new ConverterProcessorFactory().getProcessor(fileType);
        try {
            new LoggingProcessorDecorator(startingProcessor).process(context);  //process the file first

            for (Processor processor : processors) {
                processor.process(context);
            }
        } catch (Exception e) {
            throw e; // Re-throwing the exception for the caller to handle
        }

        return context.getFinalMp3Path();
    }

    private void addProcessor(Processor processor) {
        if (processor.getClass().isAnnotationPresent(CriticalProcessor.class)) {
            int retries = processor.getClass().getAnnotation(CriticalProcessor.class).retryCount();
            processors.add(new RetryableProcessorDecorator(processor, retries));
        } else {
            processors.add(processor);
        }
    }
}

