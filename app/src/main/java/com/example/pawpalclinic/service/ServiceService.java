package com.example.pawpalclinic.service;

import android.content.Context;
import android.util.Log;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Service;

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

public class ServiceService {

    private final OkHttpClient client = new OkHttpClient();
    private final String API_URL;

    public ServiceService(Context context) {
        this.API_URL = context.getString(R.string.api_base_url) + ":4332/api/public/services";
    }

    // Get all services
    public CompletableFuture<List<Service>> getAllServices() {
        CompletableFuture<List<Service>> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(API_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ServiceService", "Error fetching services", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        List<Service> serviceList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Service service = new Service(
                                    jsonObject.getInt("id"),
                                    jsonObject.getString("nomService"),
                                    jsonObject.optString("description"),
                                    jsonObject.optDouble("prix")
                            );
                            serviceList.add(service);
                        }
                        future.complete(serviceList);
                    } catch (Exception e) {
                        Log.e("ServiceService", "Error parsing JSON", e);
                        future.completeExceptionally(e);
                    }
                } else {
                    Log.e("ServiceService", "Unexpected code " + response);
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                }
            }
        });
        return future;
    }

    // Get service by ID
    public CompletableFuture<Service> getServiceById(int id) {
        CompletableFuture<Service> future = new CompletableFuture<>();
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
                        Service service = new Service(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomService"),
                                jsonObject.optString("description"),
                                jsonObject.optDouble("prix")
                        );
                        future.complete(service);
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

    // Create new service
    public CompletableFuture<Service> createService(Service service) {
        CompletableFuture<Service> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", service.getId());
            jsonObject.put("nomService", service.getNomService());
            jsonObject.put("description", service.getDescription());
            jsonObject.put("prix", service.getPrix());
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
                        Service createdService = new Service(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomService"),
                                jsonObject.optString("description"),
                                jsonObject.optDouble("prix")
                        );
                        future.complete(createdService);
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

    // Update existing service
    public CompletableFuture<Service> updateService(int id, Service service) {
        CompletableFuture<Service> future = new CompletableFuture<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", service.getId());
            jsonObject.put("nomService", service.getNomService());
            jsonObject.put("description", service.getDescription());
            jsonObject.put("prix", service.getPrix());
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
                        Service updatedService = new Service(
                                jsonObject.getInt("id"),
                                jsonObject.getString("nomService"),
                                jsonObject.optString("description"),
                                jsonObject.optDouble("prix")
                        );
                        future.complete(updatedService);
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

    // Delete service by ID
    public CompletableFuture<Void> deleteService(int id) {
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