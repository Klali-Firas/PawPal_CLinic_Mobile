package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Produit;

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

public class ProduitService {

    private final OkHttpClient client = new OkHttpClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    private String API_URL;

    public ProduitService(Context context) {
        this.API_URL = context.getString(R.string.api_base_url) + ":4332/api/public/produits";
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    // Get all products
    public CompletableFuture<List<Produit>> getAllProduits() {
        CompletableFuture<List<Produit>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ProduitService", "Error fetching products", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Produit> produitList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                            Produit produit = new Produit(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("nomProduit"),
                                    jsonObject.optString("description"),
                                    jsonObject.getDouble("prix"),
                                    jsonObject.getInt("quantiteStock"),
                                    creeLe,
                                    jsonObject.getString("image"),
                                    jsonObject.optInt("quantity")
                            );
                            produitList.add(produit);
                        }
                        future.complete(produitList);
                    } catch (Exception e) {
                        Log.e("ProduitService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("ProduitService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get product by ID
    public CompletableFuture<Produit> getProduitById(int id) {
        CompletableFuture<Produit> future = new CompletableFuture<>();
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
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Produit produit = new Produit(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomProduit"),
                                jsonObject.optString("description"),
                                jsonObject.getDouble("prix"),
                                jsonObject.getInt("quantiteStock"),
                                creeLe,
                                jsonObject.getString("image"),
                                jsonObject.optInt("quantity")
                        );
                        future.complete(produit);
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

    // Create new product
    public CompletableFuture<Produit> createProduit(Produit produit) {
        CompletableFuture<Produit> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", produit.getId());
            jsonObject.put("nomProduit", produit.getNomProduit());
            jsonObject.put("description", produit.getDescription());
            jsonObject.put("prix", produit.getPrix());
            jsonObject.put("quantiteStock", produit.getQuantiteStock());
            jsonObject.put("creeLe", dateFormat.format(produit.getCreeLe()));
            jsonObject.put("image", produit.getImage());
            jsonObject.put("quantity", produit.getQuantity());
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
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Produit createdProduit = new Produit(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomProduit"),
                                jsonObject.optString("description"),
                                jsonObject.getDouble("prix"),
                                jsonObject.getInt("quantiteStock"),
                                creeLe,
                                jsonObject.getString("image"),
                                jsonObject.optInt("quantity")
                        );
                        future.complete(createdProduit);
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

    // Update existing product
    public CompletableFuture<Produit> updateProduit(int id, Produit produit) {
        CompletableFuture<Produit> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", produit.getId());
            jsonObject.put("nomProduit", produit.getNomProduit());
            jsonObject.put("description", produit.getDescription());
            jsonObject.put("prix", produit.getPrix());
            jsonObject.put("quantiteStock", produit.getQuantiteStock());
            jsonObject.put("creeLe", dateFormat.format(produit.getCreeLe()));
            jsonObject.put("image", produit.getImage());
            jsonObject.put("quantity", produit.getQuantity());
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
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Produit updatedProduit = new Produit(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomProduit"),
                                jsonObject.optString("description"),
                                jsonObject.getDouble("prix"),
                                jsonObject.getInt("quantiteStock"),
                                creeLe,
                                jsonObject.getString("image"),
                                jsonObject.optInt("quantity")
                        );
                        future.complete(updatedProduit);
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

    // Delete product by ID
    public CompletableFuture<Void> deleteProduit(int id) {
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
}