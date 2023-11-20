//package edu.vt.codewaveservice.mq;
//
//import com.google.gson.Gson;
//import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//
//@Service
//public class TaskListenerFactory {
//
//    private final Map<String, TaskProcessingStrategy> strategies;
//    private final Map<String, ExecutorService> executorServices;
//
//    private final Gson gson = new Gson();
//
//    @Autowired
//    public TaskListenerFactory(Map<String, TaskProcessingStrategy> strategies,
//                               Map<String, ExecutorService> executorServices) {
//        this.strategies = strategies;
//        this.executorServices = executorServices;
//    }
//
//    public TaskListener createListener(String modelType) {
//        TaskProcessingStrategy strategy = strategies.get(modelType);
//        ExecutorService executorService = executorServices.get(modelType);
//
//        if (strategy == null || executorService == null) {
//            throw new IllegalArgumentException("No strategy or executor service found for model type: " + modelType);
//        }
//
//        return new TaskListener(strategy, executorService, gson);
//    }
//}
//
