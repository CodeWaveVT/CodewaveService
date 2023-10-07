package edu.vt.codewaveservice.processor;

import edu.vt.codewaveservice.processor.impl.PdfConverterProcessor;

public class ProcessorException extends RuntimeException {
    private final Processor failedProcessor;

    public ProcessorException(Processor failedProcessor, Throwable cause) {
        super(cause);
        this.failedProcessor = failedProcessor;
    }

    public ProcessorException(String noDataAvailableForProcessing, PdfConverterProcessor pdfConverterProcessor) {
        failedProcessor = null;
    }

    public Processor getFailedProcessor() {
        return failedProcessor;
    }
}

