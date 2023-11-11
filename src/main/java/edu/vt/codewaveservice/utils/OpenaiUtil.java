package edu.vt.codewaveservice.utils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import com.google.gson.JsonObject;

public class OpenaiUtil {

    private static final String OPENAI_URL = "https://api.openai.com/v1/audio/speech";
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

//    public static void main(String[] args) {
//        String base64EncodedMP3 = textToSpeech("The quick brown fox jumped over the lazy dog.");
//        System.out.println(base64EncodedMP3);
//    }
}
