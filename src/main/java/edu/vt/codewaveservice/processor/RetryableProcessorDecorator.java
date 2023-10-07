package edu.vt.codewaveservice.processor;

public class RetryableProcessorDecorator implements Processor {

    private final Processor delegate;
    private final int retryCount;

    public RetryableProcessorDecorator(Processor delegate, int retryCount) {
        this.delegate = delegate;
        this.retryCount = retryCount;
    }

    @Override
    public void process(ProcessingContext context) {
        int attempts = 0;
        while (attempts <= retryCount) {
            try {
                delegate.process(context);
                return;  // Exit if processing is successful
            } catch (Exception e) {
                if (attempts == retryCount) {
                    throw new ProcessorException(delegate, e);  // Wrap the exception with the failed processor
                }
                attempts++;
                // Optionally: sleep for some time before retrying
                // Thread.sleep(1000);  // Example: sleep for 1 second
            }
        }
    }

}

