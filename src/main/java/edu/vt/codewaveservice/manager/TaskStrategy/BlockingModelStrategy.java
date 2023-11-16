package edu.vt.codewaveservice.manager.TaskStrategy;

import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
import edu.vt.codewaveservice.model.entity.Task;

import java.util.concurrent.ExecutorService;

public class BlockingModelStrategy implements TaskProcessingStrategy {
    @Override
    public void process(Task task, ExecutorService executorService) {
        System.out.printf("BlockingModelStrategy.process()!");
    }
}
