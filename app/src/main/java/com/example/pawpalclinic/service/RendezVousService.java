package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.RendezVous;

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

public class RendezVousService {

    private final String API_URL;
    private final OkHttpClient client = new OkHttpClient();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
    private final SimpleDateFormat dateRendezVousFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);


    public RendezVousService(Context c) {
        this.API_URL = c.getString(R.string.api_base_url) + ":4332/api/public/rendezvous";

    }

    // Get all rendezvous
    public CompletableFuture<List<RendezVous>> getAllRendezVous() {
        CompletableFuture<List<RendezVous>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RendezVousService", "Error fetching rendezvous", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<RendezVous> rendezVousList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date dateRendezVous = dateRendezVousFormat.parse(jsonObject.getString("dateRendezVous"));
                            Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                            RendezVous rendezVous = new RendezVous(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("animalId"),
                                    jsonObject.has("veterinaireId") && !jsonObject.isNull("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                    dateRendezVous,
                                    jsonObject.getString("statut"),
                                    jsonObject.getInt("motif"),
                                    creeLe,
                                    jsonObject.has("remarques") && !jsonObject.isNull("remarques") ? jsonObject.getString("remarques") : null
                            );
                            rendezVousList.add(rendezVous);
                        }
                        future.complete(rendezVousList);
                    } catch (Exception e) {
                        Log.e("RendezVousService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("RendezVousService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }


    // Assign veterinaire
    public CompletableFuture<RendezVous> assignVeterinaire(int rendezVousId, int veterinaireId) {
        CompletableFuture<RendezVous> future = new CompletableFuture<>();
        String url = API_URL + "/" + rendezVousId + "/assign-veterinaire/" + veterinaireId;
        Request request = new Request.Builder().url(url).put(RequestBody.create(null, new byte[0])).build();
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
                        Date dateRendezVous = dateFormat.parse(jsonObject.getString("dateRendezVous"));
                        Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                        RendezVous rendezVous = new RendezVous(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("animalId"),
                                jsonObject.has("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                dateRendezVous,
                                jsonObject.getString("statut"),
                                jsonObject.getInt("motif"),
                                creeLe,
                                jsonObject.has("remarques") ? jsonObject.getString("remarques") : null
                        );
                        future.complete(rendezVous);
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

    // Create new rendezvous
    public CompletableFuture<RendezVous> createRendezVous(RendezVous rendezVous) {
        CompletableFuture<RendezVous> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", rendezVous.getId());
            jsonObject.put("animalId", rendezVous.getAnimalId());
            jsonObject.put("veterinaireId", rendezVous.getVeterinaireId());
            jsonObject.put("dateRendezVous", dateRendezVousFormat.format(rendezVous.getDateRendezVous()));
            jsonObject.put("statut", rendezVous.getStatut());
            jsonObject.put("motif", rendezVous.getMotif());
            jsonObject.put("creeLe", dateFormat.format(rendezVous.getCreeLe()));
            jsonObject.put("remarques", rendezVous.getRemarques());
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
                        Date dateRendezVous = dateFormat.parse(jsonObject.getString("dateRendezVous"));
                        Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                        RendezVous createdRendezVous = new RendezVous(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("animalId"),
                                jsonObject.has("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                dateRendezVous,
                                jsonObject.getString("statut"),
                                jsonObject.getInt("motif"),
                                creeLe,
                                jsonObject.has("remarques") ? jsonObject.getString("remarques") : null
                        );
                        future.complete(createdRendezVous);
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

    // Get rendezvous by veterinaire ID
    public CompletableFuture<List<RendezVous>> getRendezVousByVeterinaireId(int veterinaireId) {
        CompletableFuture<List<RendezVous>> future = new CompletableFuture<>();
        String url = API_URL + "/veterinaire/" + veterinaireId;
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
                        List<RendezVous> rendezVousList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date dateRendezVous = dateFormat.parse(jsonObject.getString("dateRendezVous"));
                            Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                            RendezVous rendezVous = new RendezVous(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("animalId"),
                                    jsonObject.has("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                    dateRendezVous,
                                    jsonObject.getString("statut"),
                                    jsonObject.getInt("motif"),
                                    creeLe,
                                    jsonObject.has("remarques") ? jsonObject.getString("remarques") : null
                            );
                            rendezVousList.add(rendezVous);
                        }
                        future.complete(rendezVousList);
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

    // Update existing rendezvous
    public CompletableFuture<RendezVous> updateRendezVous(int id, RendezVous rendezVous) {
        CompletableFuture<RendezVous> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", rendezVous.getId());
            jsonObject.put("animalId", rendezVous.getAnimalId());
            jsonObject.put("veterinaireId", rendezVous.getVeterinaireId());
            jsonObject.put("dateRendezVous", dateFormat.format(rendezVous.getDateRendezVous()));
            jsonObject.put("statut", rendezVous.getStatut());
            jsonObject.put("motif", rendezVous.getMotif());
            jsonObject.put("creeLe", dateFormat.format(rendezVous.getCreeLe()));
            jsonObject.put("remarques", rendezVous.getRemarques());
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
                        Date dateRendezVous = dateFormat.parse(jsonObject.getString("dateRendezVous"));
                        Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                        RendezVous updatedRendezVous = new RendezVous(
                                jsonObject.getInt("id"),
                                jsonObject.getInt("animalId"),
                                jsonObject.has("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                dateRendezVous,
                                jsonObject.getString("statut"),
                                jsonObject.getInt("motif"),
                                creeLe,
                                jsonObject.has("remarques") ? jsonObject.getString("remarques") : null
                        );
                        future.complete(updatedRendezVous);
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

    // Get rendezvous by user ID
    public CompletableFuture<List<RendezVous>> getRendezVousByUserId(int userId) {
        CompletableFuture<List<RendezVous>> future = new CompletableFuture<>();
        String url = API_URL + "/user/" + userId;
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
                        List<RendezVous> rendezVousList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Date dateRendezVous = dateFormat.parse(jsonObject.getString("dateRendezVous"));
                            Date creeLe = jsonObject.has("creeLe") ? dateFormat.parse(jsonObject.getString("creeLe")) : null;
                            RendezVous rendezVous = new RendezVous(
                                    jsonObject.getInt("id"),
                                    jsonObject.getInt("animalId"),
                                    jsonObject.has("veterinaireId") ? jsonObject.getInt("veterinaireId") : null,
                                    dateRendezVous,
                                    jsonObject.getString("statut"),
                                    jsonObject.getInt("motif"),
                                    creeLe,
                                    jsonObject.has("remarques") ? jsonObject.getString("remarques") : null
                            );
                            rendezVousList.add(rendezVous);
                        }
                        future.complete(rendezVousList);
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

    // Export rendezvous to CSV
    public CompletableFuture<byte[]> exportRendezVousToCsv() {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        String url = API_URL + "/export/csv";
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    future.complete(response.body().bytes());
                } else {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }
}