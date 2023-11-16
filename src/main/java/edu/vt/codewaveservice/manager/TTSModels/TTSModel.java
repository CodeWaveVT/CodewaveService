package edu.vt.codewaveservice.manager.TTSModels;

import java.io.IOException;

public interface TTSModel {
    String generateAudio(String text) throws IOException;
}
