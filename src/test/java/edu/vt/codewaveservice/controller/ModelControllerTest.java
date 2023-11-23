package edu.vt.codewaveservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import edu.vt.codewaveservice.manager.TTSModels.TTSModelProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

class ModelControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TTSModelProperties ttsModelProperties;

    @InjectMocks
    private ModelController modelController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(modelController).build();
    }

    @Test
    public void testList() throws Exception {
        Map<String, TTSModelProperties.ModelDetails> models = new HashMap<>();
        models.put("model1", new TTSModelProperties.ModelDetails());
        when(ttsModelProperties.getModels()).thenReturn(models);

        mockMvc.perform(get("/model/list"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"code\":20000,\"data\":[\"model1\"],\"message\":\"ok\"}"));

        verify(ttsModelProperties, times(1)).getModels();
    }
}