package edu.vt.codewaveservice.manager;

import edu.vt.codewaveservice.model.entity.Task;

import java.util.concurrent.ExecutorService;

public interface TaskProcessingStrategy {
    void process(Task task, ExecutorService executorService);
}

