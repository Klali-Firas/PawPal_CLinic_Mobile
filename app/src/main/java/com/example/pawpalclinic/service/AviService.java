package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Avi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AviService {

    private final OkHttpClient client = new OkHttpClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    private String API_URL;

    public AviService(Context context) {
        this.API_URL = context.getString(R.string.api_base_url) + ":4332/api/public/avis";
    }

    // Get all avis
    public CompletableFuture<List<Avi>> getAllAvis() {
        CompletableFuture<List<Avi>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AviService", "Error fetching avis", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Avi> aviList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                            Avi avi = new Avi(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("rendezVousId"),
                                    jsonObject.optInt("note"),
                                    jsonObject.optString("commentaire"),
                                    creeLe,
                                    jsonObject.getInt("proprietaireId")
                            );
                            aviList.add(avi);
                        }
                        future.complete(aviList);
                    } catch (Exception e) {
                        Log.e("AviService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("AviService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get avi by ID
    public CompletableFuture<Avi> getAviById(int id) {
        CompletableFuture<Avi> future = new CompletableFuture<>();
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
                        Avi avi = new Avi(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("rendezVousId"),
                                jsonObject.optInt("note"),
                                jsonObject.optString("commentaire"),
                                creeLe,
                                jsonObject.getInt("proprietaireId")
                        );
                        future.complete(avi);
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

    // Create new avi
    public CompletableFuture<Avi> createAvi(Avi avi) {
        CompletableFuture<Avi> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", avi.getId());
            jsonObject.put("rendezVousId", avi.getRendezVousId());
            jsonObject.put("note", avi.getNote());
            jsonObject.put("commentaire", avi.getCommentaire());
            jsonObject.put("creeLe", dateFormat.format(avi.getCreeLe()));
            jsonObject.put("proprietaireId", avi.getProprietaireId());
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );
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
                        Avi createdAvi = new Avi(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("rendezVousId"),
                                jsonObject.optInt("note"),
                                jsonObject.optString("commentaire"),
                                creeLe,
                                jsonObject.getInt("proprietaireId")
                        );
                        future.complete(createdAvi);
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

    // Update existing avi
    public CompletableFuture<Avi> updateAvi(int id, Avi avi) {
        CompletableFuture<Avi> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", avi.getId());
            jsonObject.put("rendezVousId", avi.getRendezVousId());
            jsonObject.put("note", avi.getNote());
            jsonObject.put("commentaire", avi.getCommentaire());
            jsonObject.put("creeLe", dateFormat.format(avi.getCreeLe()));
            jsonObject.put("proprietaireId", avi.getProprietaireId());
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );
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
                        Avi updatedAvi = new Avi(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("rendezVousId"),
                                jsonObject.optInt("note"),
                                jsonObject.optString("commentaire"),
                                creeLe,
                                jsonObject.getInt("proprietaireId")
                        );
                        future.complete(updatedAvi);
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

    // Delete avi by ID
    public CompletableFuture<Void> deleteAvi(int id) {
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

    // Get avis by rendezvous ID
    public CompletableFuture<List<Avi>> getAvisByRendezVousId(int rendezVousId) {
        CompletableFuture<List<Avi>> future = new CompletableFuture<>();
        String url = API_URL + "/rendezvous/" + rendezVousId;
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
                        List<Avi> aviList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                            Avi avi = new Avi(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("rendezVousId"),
                                    jsonObject.optInt("note"),
                                    jsonObject.optString("commentaire"),
                                    creeLe,
                                    jsonObject.getInt("proprietaireId")
                            );
                            aviList.add(avi);
                        }
                        future.complete(aviList);
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

    // Get avi by rendezvous ID and proprietaire ID
    public CompletableFuture<Avi> getAviByRendezVousIdAndProprietaireId(int rendezVousId, int proprietaireId) {
        CompletableFuture<Avi> future = new CompletableFuture<>();
        String url = API_URL + "/rendezvous/" + rendezVousId + "/proprietaire/" + proprietaireId;
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
                        Date creeLe = dateFormat.parse(jsonObject.getString("creeLe"));
                        Avi avi = new Avi(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("rendezVousId"),
                                jsonObject.optInt("note"),
                                jsonObject.optString("commentaire"),
                                creeLe,
                                jsonObject.getInt("proprietaireId")
                        );
                        future.complete(avi);
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