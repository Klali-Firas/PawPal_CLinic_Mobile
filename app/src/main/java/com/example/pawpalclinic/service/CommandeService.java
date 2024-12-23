package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Commande;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommandeService {

    private final OkHttpClient client = new OkHttpClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    private final SimpleDateFormat secondDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    private String API_URL;


    public CommandeService(Context context) {
        this.API_URL = context.getString(R.string.api_base_url) + ":4332/api/public/commandes";
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    // Get all commandes
    public CompletableFuture<List<Commande>> getAllCommandes() {
        CompletableFuture<List<Commande>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("CommandeService", "Error fetching commandes", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Commande> commandeList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date dateCommande = dateFormat.parse(jsonObject.getString("dateCommande"));
                            Commande commande = new Commande(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("proprietaireId"),
                                    dateCommande,
                                    jsonObject.getString("statut")
                            );
                            commandeList.add(commande);
                        }
                        future.complete(commandeList);
                    } catch (Exception e) {
                        Log.e("CommandeService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("CommandeService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get commande by ID
    public CompletableFuture<Commande> getCommandeById(int id) {
        CompletableFuture<Commande> future = new CompletableFuture<>();
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
                        Date dateCommande = dateFormat.parse(jsonObject.getString("dateCommande"));
                        Commande commande = new Commande(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                dateCommande,
                                jsonObject.getString("statut")
                        );
                        future.complete(commande);
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

    // Create new commande
    public CompletableFuture<Commande> createCommande(Commande commande) {
        CompletableFuture<Commande> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", commande.getId());
            jsonObject.put("proprietaireId", commande.getProprietaireId());
            jsonObject.put("dateCommande", dateFormat.format(commande.getDateCommande()));
            jsonObject.put("statut", commande.getStatut());
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
                        Date dateCommande = dateFormat.parse(jsonObject.getString("dateCommande"));
                        Commande createdCommande = new Commande(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                dateCommande,
                                jsonObject.getString("statut")
                        );
                        future.complete(createdCommande);
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

    // Update existing commande
    public CompletableFuture<Commande> updateCommande(int id, Commande commande) {
        CompletableFuture<Commande> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", commande.getId());
            jsonObject.put("proprietaireId", commande.getProprietaireId());
            jsonObject.put("dateCommande", dateFormat.format(commande.getDateCommande()));
            jsonObject.put("statut", commande.getStatut());
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
                        Date dateCommande = secondDateFormat.parse(jsonObject.getString("dateCommande"));
                        Commande updatedCommande = new Commande(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("proprietaireId"),
                                dateCommande,
                                jsonObject.getString("statut")
                        );
                        future.complete(updatedCommande);
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

    // Delete commande by ID
    public CompletableFuture<Void> deleteCommande(int id) {
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

    // Get commandes by proprietaire ID
    public CompletableFuture<List<Commande>> getCommandesByProprietaireId(int proprietaireId) {
        CompletableFuture<List<Commande>> future = new CompletableFuture<>();
        String url = API_URL + "/user/" + proprietaireId;
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
                        List<Commande> commandeList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date dateCommande = dateFormat.parse(jsonObject.getString("dateCommande"));
                            Commande commande = new Commande(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("proprietaireId"),
                                    dateCommande,
                                    jsonObject.getString("statut")
                            );
                            commandeList.add(commande);
                        }
                        future.complete(commandeList);
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