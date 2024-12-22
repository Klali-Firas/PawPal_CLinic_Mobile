package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.CommandeProduit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommandeProduitService {

    private final OkHttpClient client = new OkHttpClient();
    private String API_URL;

    public CommandeProduitService(Context context) {
        this.API_URL = context.getString(R.string.api_base_url) + "/api/public/commande-produits";
    }

    // Get all commande produits
    public CompletableFuture<List<CommandeProduit>> getAllCommandeProduits() {
        CompletableFuture<List<CommandeProduit>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CommandeProduitService", "Error fetching commande produits", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<CommandeProduit> commandeProduitList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommandeProduit commandeProduit = new CommandeProduit(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("commandeId"),
                                    jsonObject.getInt("produitId"),
                                    jsonObject.getInt("quantite")
                            );
                            commandeProduitList.add(commandeProduit);
                        }
                        future.complete(commandeProduitList);
                    } catch (Exception e) {
                        Log.e("CommandeProduitService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("CommandeProduitService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get commande produit by ID
    public CompletableFuture<CommandeProduit> getCommandeProduitById(int id) {
        CompletableFuture<CommandeProduit> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL + "/" + id).build();
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
                        CommandeProduit commandeProduit = new CommandeProduit(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("commandeId"),
                                jsonObject.getInt("produitId"),
                                jsonObject.getInt("quantite")
                        );
                        future.complete(commandeProduit);
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

    // Create new commande produit
    public CompletableFuture<CommandeProduit> createCommandeProduit(CommandeProduit commandeProduit) {
        CompletableFuture<CommandeProduit> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", commandeProduit.getId());
            jsonObject.put("commandeId", commandeProduit.getCommandeId());
            jsonObject.put("produitId", commandeProduit.getProduitId());
            jsonObject.put("quantite", commandeProduit.getQuantite());
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder().url(API_URL).post(body).build();
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
                        CommandeProduit createdCommandeProduit = new CommandeProduit(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("commandeId"),
                                jsonObject.getInt("produitId"),
                                jsonObject.getInt("quantite")
                        );
                        future.complete(createdCommandeProduit);
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

    // Update existing commande produit
    public CompletableFuture<CommandeProduit> updateCommandeProduit(int id, CommandeProduit commandeProduit) {
        CompletableFuture<CommandeProduit> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", commandeProduit.getId());
            jsonObject.put("commandeId", commandeProduit.getCommandeId());
            jsonObject.put("produitId", commandeProduit.getProduitId());
            jsonObject.put("quantite", commandeProduit.getQuantite());
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        Request request = new Request.Builder().url(API_URL + "/" + id).put(body).build();
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
                        CommandeProduit updatedCommandeProduit = new CommandeProduit(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("commandeId"),
                                jsonObject.getInt("produitId"),
                                jsonObject.getInt("quantite")
                        );
                        future.complete(updatedCommandeProduit);
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

    // Delete commande produit by ID
    public CompletableFuture<Void> deleteCommandeProduit(int id) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL + "/" + id).delete().build();
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

    // Get commande produits by commande ID
    public CompletableFuture<List<CommandeProduit>> getCommandeProduitsByCommandeId(int commandeId) {
        CompletableFuture<List<CommandeProduit>> future = new CompletableFuture<>();
        String url = API_URL + "/commande/" + commandeId;
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
                        List<CommandeProduit> commandeProduitList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CommandeProduit commandeProduit = new CommandeProduit(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("commandeId"),
                                    jsonObject.getInt("produitId"),
                                    jsonObject.getInt("quantite")
                            );
                            commandeProduitList.add(commandeProduit);
                        }
                        future.complete(commandeProduitList);
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