package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Utilisateur;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UtilisateurService {

    private  final String API_URL;
    private final OkHttpClient client = new OkHttpClient();

    public UtilisateurService(Context c) {
        this.API_URL = c.getString(R.string.api_base_url) + "/api/public/utilisateurs";
    }

    // Get veterinaire by ID
    public CompletableFuture<Utilisateur> getVeterinaireById(int id) {
        CompletableFuture<Utilisateur> future = new CompletableFuture<>();
        String url = API_URL + "/veterinaire/" + id;
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Utilisateur utilisateur = new Utilisateur(
                                jsonObject.getInt("id"),
                                jsonObject.getString("email"),
                                jsonObject.getString("role"),
                                jsonObject.optString("prenom", null),
                                jsonObject.optString("nom", null),
                                jsonObject.optString("telephone", null),
                                jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                        );
                        future.complete(utilisateur);
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

    // Get utilisateur by ID
    public CompletableFuture<Utilisateur> getUtilisateurById(int id) {
        CompletableFuture<Utilisateur> future = new CompletableFuture<>();
        String url = API_URL + "/" + id;
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Utilisateur utilisateur = new Utilisateur(
                                jsonObject.getInt("id"),
                                jsonObject.getString("email"),
                                jsonObject.getString("role"),
                                jsonObject.optString("prenom", null),
                                jsonObject.optString("nom", null),
                                jsonObject.optString("telephone", null),
                                jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                        );
                        future.complete(utilisateur);
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

    // Get proprietaire by animal ID
    public CompletableFuture<Utilisateur> getProprietaireByAnimalId(int animalId) {
        CompletableFuture<Utilisateur> future = new CompletableFuture<>();
        String url = API_URL + "/proprietaire/animal/" + animalId;
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Utilisateur utilisateur = new Utilisateur(
                                jsonObject.getInt("id"),
                                jsonObject.getString("email"),
                                jsonObject.getString("role"),
                                jsonObject.optString("prenom", null),
                                jsonObject.optString("nom", null),
                                jsonObject.optString("telephone", null),
                                jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                        );
                        future.complete(utilisateur);
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

    // Get all veterinaires
    public CompletableFuture<List<Utilisateur>> getAllVeterinaires() {
        CompletableFuture<List<Utilisateur>> future = new CompletableFuture<>();
        String url = API_URL + "/veterinaires";
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
                        List<Utilisateur> utilisateursList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Utilisateur utilisateur = new Utilisateur(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("role"),
                                    jsonObject.optString("prenom", null),
                                    jsonObject.optString("nom", null),
                                    jsonObject.optString("telephone", null),
                                    jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                            );
                            utilisateursList.add(utilisateur);
                        }
                        future.complete(utilisateursList);
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

    // Get all utilisateurs
    public CompletableFuture<List<Utilisateur>> getAllUtilisateurs() {
        CompletableFuture<List<Utilisateur>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
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
                        List<Utilisateur> utilisateursList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Utilisateur utilisateur = new Utilisateur(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("email"),
                                    jsonObject.getString("role"),
                                    jsonObject.optString("prenom", null),
                                    jsonObject.optString("nom", null),
                                    jsonObject.optString("telephone", null),
                                    jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                            );
                            utilisateursList.add(utilisateur);
                        }
                        future.complete(utilisateursList);
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

    // Get logged in user
    public CompletableFuture<Utilisateur> getLoggedInUser() {
        CompletableFuture<Utilisateur> future = new CompletableFuture<>();
        String url = API_URL + "/loggedInUser";
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Utilisateur utilisateur = new Utilisateur(
                                jsonObject.getInt("id"),
                                jsonObject.getString("email"),
                                jsonObject.getString("role"),
                                jsonObject.optString("prenom", null),
                                jsonObject.optString("nom", null),
                                jsonObject.optString("telephone", null),
                                jsonObject.has("creeLe") ? new Date(jsonObject.getString("creeLe")) : null
                        );
                        future.complete(utilisateur);
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