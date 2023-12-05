package edu.vt.codewaveservice.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class YashUtil {

    public static String getAudio(String text,String fileName,String modelType){
//        try{
//            Thread.sleep(60000);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return "https://pub-c2333b24d1fd411281061ef45185b82e.r2.dev/test_Through_the_First_Antarctic_Night_1_audiobook_0.wav";
        String audioUrl = "";
        try {
            audioUrl = sendGetRequest(text,fileName,modelType);
        }catch (Exception e){
            e.printStackTrace();
        }
        return audioUrl;
    }

    public static String extractCharacter(String input) {
        return input != null ? input.substring(input.lastIndexOf("_") + 1) : "";
    }

    public static String sendGetRequest(String text,String fileName,String modelType) throws Exception {
        String character = extractCharacter(modelType);
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        String encodedModeCharacter = URLEncoder.encode(character, StandardCharsets.UTF_8.toString());
        String url = String.format("https://d944-2607-b400-802-8002-4858-8fdc-d426-7263.ngrok-free.app/v1/audio/tts/?text=%s&audiobook_name=%s&language=en&voice_name=%s&preset=fast", encodedText, encodedFileName, encodedModeCharacter);


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        String audioUrl = jsonResponse.get("audio").getAsString();
        return audioUrl;
    }

//    public static void main(String[] args) {
//        try {
//            String text = "test from springboot 2";
//            String getResponse = sendGetRequest(text,"test2","Scoop");
//            System.out.println("GET Response: " + getResponse);
//
////            String postResponse = sendPostRequest(text);
////            System.out.println("POST Response: " + postResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
