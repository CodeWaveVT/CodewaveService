package edu.vt.codewaveservice.processor;

import edu.vt.codewaveservice.processor.impl.*;
import edu.vt.codewaveservice.utils.TempFileManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class TextToAudioChain {
    private List<Processor> processors = new ArrayList<>();

    public TextToAudioChain() {
        processors.add(new LoggingProcessorDecorator(new TextPreprocessor()));
        processors.add(new LoggingProcessorDecorator(new TextFileWriter()));
        processors.add(new LoggingProcessorDecorator(new TextSplitter()));
        processors.add(new LoggingProcessorDecorator(new AudioGenerator()));
        processors.add(new LoggingProcessorDecorator(new AudioMerger()));
        processors.add(new LoggingProcessorDecorator(new S3Uploader()));
    }

    public String process(ProcessingContext context) throws Exception {
        String fileType = new ConverterProcessorFactory().getFileExtension(context.getFile().getOriginalFilename());
        System.out.println(fileType);
        Processor startingProcessor = new ConverterProcessorFactory().getProcessor(fileType);
        try {
            new LoggingProcessorDecorator(startingProcessor).process(context);  // process the file first
            for (Processor processor : processors) {
                processor.process(context);
            }
        } catch (Exception e) {
            throw e; // Re-throwing the exception for the caller to handle
        }

        return context.getFinalMp3Path();
    }
}

