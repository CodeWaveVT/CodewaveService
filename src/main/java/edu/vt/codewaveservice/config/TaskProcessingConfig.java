package edu.vt.codewaveservice.config;

import edu.vt.codewaveservice.manager.TTSModels.TTSModelProperties;
import edu.vt.codewaveservice.manager.TaskDispatcher;
import edu.vt.codewaveservice.manager.TaskProcessingStrategy;
import edu.vt.codewaveservice.manager.TaskStrategy.BlockingModelStrategy;
import edu.vt.codewaveservice.manager.TaskStrategy.ConcurrentModelStrategy;
import edu.vt.codewaveservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class TaskProcessingConfig {

    @Autowired
    private TTSModelProperties ttsModelProperties;

    @Bean(name = "executorServices")
    public Map<String, ExecutorService> executorServices() {
//        Map<String, ExecutorService> executorServices = new HashMap<>();
//        executorServices.put("openai", Executors.newFixedThreadPool(10));
//        executorServices.put("xunfei", Executors.newSingleThreadExecutor());
        //executorServices.forEach((key, value) -> log.info("Key E: {}, ExecutorService: {}", key, value));
        Map<String, ExecutorService> executorServices = new HashMap<>();
        ttsModelProperties.getModels().forEach((modelName, modelDetails) -> {
            ExecutorService executorService = Executors.newFixedThreadPool(modelDetails.getConcurrency());
            executorServices.put(modelName, executorService);
            log.info("Executor service created for model {}: {}", modelName, executorService);
        });
        return executorServices;
    }

    @Bean(name = "strategies")
    public Map<String, TaskProcessingStrategy> strategies(TaskService taskService) {
        Map<String, TaskProcessingStrategy> strategies = new HashMap<>();
        Map<String, TTSModelProperties.ModelDetails> models = ttsModelProperties.getModels();
        for(String modelName : models.keySet()) {
            if(modelName.contains("yash")){
                strategies.put(modelName, new BlockingModelStrategy(taskService));
            }else{
                strategies.put(modelName, new ConcurrentModelStrategy(taskService));
            }
        }
//        strategies.put("openai", new ConcurrentModelStrategy(taskService));
//        strategies.put("xunfei", new ConcurrentModelStrategy(taskService));
        strategies.forEach((key, value) -> log.info("Key S: {}, Strategy: {}", key, value.getClass().getSimpleName()));
        return strategies;
    }

    @Bean
    public TaskDispatcher taskDispatcher(Map<String, TaskProcessingStrategy> strategies,
                                         Map<String, ExecutorService> executorServices) {
        strategies.forEach((key, value) -> log.info("Key D: {}, Strategy: {}", key, value.getClass().getSimpleName()));
        //executorServices.forEach((key, value) -> log.info("Key D: {}, ExecutorService: {}", key, value));
        TaskDispatcher dispatcher = new TaskDispatcher(strategies, executorServices);
        dispatcher.strategyMap.forEach((key, value) -> log.info("Key: {}, Strategy: {}", key, value.getClass().getSimpleName()));
       // dispatcher.executorServiceMap.forEach((key, value) -> log.info("Key: {}, ExecutorService: {}", key, value));
        return dispatcher;
    }
}
