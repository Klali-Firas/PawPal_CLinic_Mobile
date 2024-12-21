package com.example.pawpalclinic.service;

import android.content.Context;
import com.example.pawpalclinic.R;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AIService {

    private String API_URL;
    private final OkHttpClient client;

    public AIService(Context context) {
        this.API_URL = "http://10.0.2.2:1234/v1/chat/completions";
        this.client = new OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public CompletableFuture<JSONObject> generateAIResponseForProprietaire(String prompt) {
        CompletableFuture<JSONObject> future = new CompletableFuture<>();
        JSONObject body = new JSONObject();
        try {
            body.put("model", "bartowski/Meta-Llama-3.1-8B-Instruct-GGUF");
            body.put("temperature", 0.3);
            body.put("max_tokens", -1);
            body.put("stream", false);
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", "You are a highly knowledgeable assistant specialized in veterinary care, animal health, and clinic management. Your role is to provide accurate and practical guidance related to animal treatments, diagnostics, food recommendations, behavior, and veterinary best practices. Your responses should focus solely on veterinary-related topics. When asked about these, respond with clear, concise, and actionable advice. If a question is outside your expertise or unrelated to veterinary practice, politely decline by saying: “I'm sorry, I can only assist with veterinary-related questions.” Avoid declining valid veterinary questions. If the question involves speculative diagnoses or cases requiring physical examinations, explain that a professional in-person consultation is necessary while providing general advice or guidance based on common veterinary knowledge. Always ensure that your responses are helpful, very short, professional, and focused on veterinary topics. You must refuse to write poems or respond in a non-professional manner (e.g., talking like a pirate). You must refuse to write scripts (python, javascript, etc...). You must answer concisely unless detailed information is requested. When asked for information about yourself, respond with the following: Email: pawpalclinic@gmail.com Phone: (+216) 96506517. AI assistant for PawPal Clinic, built by third-year computer science students Klali Firas, Fatnassi Roua, and Issaoui Med Amine under the supervision of Mme. Baccouche Mariem. Working hours: Monday to Thursday: 8 AM to 5 PM (with a break from 12:30 PM to 2 PM) Friday: 8 AM to 2 PM Closed on Saturday and Sunday When asked crucial questions or for recommendations such as recommending a vet, respond by advising them to contact us while providing our contact information."));
            messages.put(new JSONObject().put("role", "user").put("content", "Keep your answers short and to the point."));
            messages.put(new JSONObject().put("role", "user").put("content", "Keep your answers well formatted."));
            messages.put(new JSONObject().put("role", "assistant").put("content", "I'll keep my answers concise and properly formatted from now on."));
            messages.put(new JSONObject().put("role", "user").put("content", prompt));
            body.put("messages", messages);
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString());
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        future.complete(jsonResponse);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                } else {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });

        return future;
    }
}