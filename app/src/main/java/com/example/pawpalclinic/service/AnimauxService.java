package com.example.pawpalclinic.service;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Animaux;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class AnimauxService {

    private String API_URL ;
    private final OkHttpClient client = new OkHttpClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    public AnimauxService(Context context) {
        this.API_URL =  context.getString(R.string.api_base_url) + "/api/public/animaux";
    }
    // Get all animals
    public CompletableFuture<List<Animaux>> getAllAnimaux() {
        CompletableFuture<List<Animaux>> future = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(API_URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AnimauxService", "Error fetching animals", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Animaux> animauxList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                            Animaux animaux = new Animaux(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("proprietaireId"),
                                    jsonObject.getString("nom"),
                                    jsonObject.getString("race"),
                                    jsonObject.getInt("age"),
                                    creeLe
                            );
                            animauxList.add(animaux);
                        }
                        future.complete(animauxList);
                    } catch (Exception e) {
                        Log.e("AnimauxService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("AnimauxService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get animal by ID
    public CompletableFuture<Animaux> getAnimauxById(int id) {
        CompletableFuture<Animaux> future = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Animaux animaux = new Animaux(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                jsonObject.getString("nom"),
                                jsonObject.getString("race"),
                                jsonObject.getInt("age"),
                                creeLe
                        );
                        future.complete(animaux);
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

    // Create new animal
    public CompletableFuture<Animaux> createAnimaux(Animaux animaux) {
        CompletableFuture<Animaux> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", animaux.getId());
            jsonObject.put("proprietaireId", animaux.getProprietaireId());
            jsonObject.put("nom", animaux.getNom());
            jsonObject.put("race", animaux.getRace());
            jsonObject.put("age", animaux.getAge());
            jsonObject.put("creeLe", dateFormat.format(animaux.getCreeLe()));
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Animaux createdAnimaux = new Animaux(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                jsonObject.getString("nom"),
                                jsonObject.getString("race"),
                                jsonObject.getInt("age"),
                                creeLe
                        );
                        future.complete(createdAnimaux);
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

    // Update existing animal
    public CompletableFuture<Animaux> updateAnimaux(int id, Animaux animaux) {
        CompletableFuture<Animaux> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", animaux.getId());
            jsonObject.put("proprietaireId", animaux.getProprietaireId());
            jsonObject.put("nom", animaux.getNom());
            jsonObject.put("race", animaux.getRace());
            jsonObject.put("age", animaux.getAge());
            jsonObject.put("creeLe", dateFormat.format(animaux.getCreeLe()));
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .put(body)
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Animaux updatedAnimaux = new Animaux(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                jsonObject.getString("nom"),
                                jsonObject.getString("race"),
                                jsonObject.getInt("age"),
                                creeLe
                        );
                        future.complete(updatedAnimaux);
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

    // Delete animal by ID
    public CompletableFuture<Void> deleteAnimaux(int id) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(API_URL + "/" + id)
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }
    public CompletableFuture<List<Animaux>> getAnimauxByProprietaireId(int proprietaireId) {
        CompletableFuture<List<Animaux>> future = new CompletableFuture<>();
        String url = API_URL + "/proprietaire/" + proprietaireId;
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Animaux> animauxList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                            Animaux animaux = new Animaux(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("proprietaireId"),
                                    jsonObject.getString("nom"),
                                    jsonObject.getString("race"),
                                    jsonObject.getInt("age"),
                                    creeLe
                            );
                            animauxList.add(animaux);
                        }
                        future.complete(animauxList);
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