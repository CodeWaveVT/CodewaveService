package edu.vt.codewaveservice.manager.TTSModels;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件中的tts模型配置映射到该类
 */
@ConfigurationProperties(prefix = "audio")
@Component
public class TTSModelProperties {

    private Map<String, ModelDetails> models = new HashMap<>();

    public Map<String, ModelDetails> getModels() {
        return models;
    }

    public void setModels(Map<String, ModelDetails> models) {
        this.models = models;
    }

    public static class ModelDetails {
        public int getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(int concurrency) {
            this.concurrency = concurrency;
        }

        public int getMaxInputLength() {
            return maxInputLength;
        }

        public void setMaxInputLength(int maxInputLength) {
            this.maxInputLength = maxInputLength;
        }

        private int concurrency;
        private int maxInputLength;
    }
}
