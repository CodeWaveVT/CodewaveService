package edu.vt.codewaveservice.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class ThreadPoolManager {

    private final Map<String, ExecutorService> executorServices = new HashMap<>();

    public void registerThreadPool(String modelType, ExecutorService executorService) {
        executorServices.put(modelType, executorService);
    }

    public ExecutorService getExecutorService(String modelType) {
        return executorServices.get(modelType);
    }

    public void shutdownAll() {
        executorServices.values().forEach(ExecutorService::shutdown);
    }
}

