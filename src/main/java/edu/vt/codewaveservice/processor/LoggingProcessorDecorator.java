package edu.vt.codewaveservice.processor;

public class LoggingProcessorDecorator implements Processor {
    private final Processor delegate;

    public LoggingProcessorDecorator(Processor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void process(ProcessingContext context) {
        try {
            long startTime = System.currentTimeMillis();
            delegate.process(context);
            long endTime = System.currentTimeMillis();

            // Logging the duration and step name for tracking
            System.out.println(String.format("Processor [%s] completed in %dms.",
                    delegate.getClass().getSimpleName(),
                    (endTime - startTime)));
        } catch (Exception e) {
            // Logging the error for diagnosis
            System.err.println(String.format("Error occurred in Processor [%s]: %s",
                    delegate.getClass().getSimpleName(),
                    e.getMessage()));
            e.printStackTrace(); // Or use a logger to log the stack trace
            throw e;  // Re-throwing the exception to halt the chain
        }
    }
}

