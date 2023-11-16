package edu.vt.codewaveservice.manager;

import edu.vt.codewaveservice.model.entity.Task;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutorService;
@Slf4j
public class TaskDispatcher {

    @PostConstruct
    public void init() {
        log.info("TaskDispatcher initialized with strategies: {}", strategyMap.keySet());
        log.info("TaskDispatcher initialized with executor services: {}", executorServiceMap.keySet());
    }

    public Map<String, TaskProcessingStrategy> strategyMap;
    public Map<String, ExecutorService> executorServiceMap;

    public TaskDispatcher(Map<String, TaskProcessingStrategy> strategyMap,
                          Map<String, ExecutorService> executorServiceMap) {
        this.strategyMap = strategyMap;
        this.executorServiceMap = executorServiceMap;
    }

    public void dispatch(Task task) {
        String modelType = task.getModelType();
        TaskProcessingStrategy strategy = strategyMap.get(modelType);
        ExecutorService executorService = executorServiceMap.get(modelType);

        if(strategy == null) {
            System.out.printf("strategy is null!");
        }

        if (executorService == null) {
            System.out.printf("executorService is null!");
        }

        if (strategy != null && executorService != null) {
            strategy.process(task, executorService);
        } else {
            throw new IllegalArgumentException("No strategy or executor service found for model type: " + modelType);
        }
    }
}
