package edu.vt.codewaveservice.manager.TTSModels;

import com.google.gson.JsonObject;
import edu.vt.codewaveservice.manager.TTSModels.TTSModel;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Component("openai")
public class OpenaiModel implements TTSModel {
    @Override
    public String generateAudio(String text) throws IOException {
        return textToSpeech(text);
    }

    private static final String OPENAI_URL = "";
    private static final String API_KEY = "";

    public static String textToSpeech(String text) {
        try {
            URL url = new URL(OPENAI_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JsonObject jsonInput = new JsonObject();
            jsonInput.addProperty("model", "tts-1");
            jsonInput.addProperty("input", text);
            jsonInput.addProperty("voice", "alloy");

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    buf.write(buffer, 0, bytesRead);
                }
                byte[] mp3Data = buf.toByteArray();
                return Base64.getEncoder().encodeToString(mp3Data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
