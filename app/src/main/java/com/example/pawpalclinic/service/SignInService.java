package com.example.pawpalclinic.service;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.view.HomePage;
import com.example.pawpalclinic.view.MainActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignInService {
    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_KEY = "user";
    private final Context context;
    private final SignInClient signInClient;
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher;
    private final String apiUrl;

    public SignInService(Context context) {
        this.context = context;
        this.signInClient = Identity.getSignInClient(context);
        this.signInLauncher = null;
        this.apiUrl = context.getString(R.string.api_base_url);
    }

    public SignInService(Context context, ActivityResultLauncher<IntentSenderRequest> signInLauncher) {
        this.context = context;
        this.signInClient = Identity.getSignInClient(context);
        this.signInLauncher = signInLauncher;
        this.apiUrl = context.getString(R.string.api_base_url);

    }

    public void signIn() {
        Log.i("SignInManager", "Signing in button clicked.");
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getString(R.string.server_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener(result -> {
                    Log.i("SignInManager", "Sign-in request successful.");
                    try {
                        signInLauncher.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                    } catch (Exception e) {
                        Log.e("SignInManager", "Couldn't start Sign In: " + e.getLocalizedMessage());
                        handleFailure(e);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SignInManager", "Sign-in request failed: " + e.getLocalizedMessage());
                    handleFailure(e);
                });
    }

    public void handleSignInResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            try {
                SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
                handleSignIn(credential);
            } catch (ApiException e) {
                Log.e("SignInManager", "ApiException: " + e.getLocalizedMessage());
                handleFailure(e);
            }
        } else {
            Log.i("SignInManager", "Result not OK. Result code: " + resultCode);
            if (data != null) {
                Log.i("SignInManager", "Intent data: " + data.toString());
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i("SignInManager", "Sign-in was canceled by the user.");
                Toast.makeText(context, "Sign-in was canceled. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("SignInManager", "Sign-in failed with unknown result code.");
            }
        }
    }

    private void handleSignIn(SignInCredential credential) {
        Log.i("SignInManager", "Handling Login.");
        String idToken = credential.getGoogleIdToken();
        if (idToken != null) {
            Log.i("SignInManager", "Got ID token.");
            Log.i("SignInManager", "ID token: " + idToken);
            postTokenToServer(idToken); // Send token to your backend via HTTPS
        } else {
            Log.i("SignInManager", "No ID token!");
        }
    }

    private void handleFailure(Exception e) {
        Log.i("SignInManager", "Sign-in failed", e);
        Toast.makeText(context, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
    }

    private void postTokenToServer(String idToken) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(apiUrl + ":4332/verifyGoogleIdToken");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = "{\"idTokenString\": \"" + idToken + "\"}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                Log.i("SignInManager", "POST Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        Log.i("SignInManager", "Response: " + response.toString());
                        Log.i("SignInManager", "POST request worked.");

                        // Parse the response and store user data in SharedPreferences
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        JSONObject userJson = jsonResponse.getJSONObject("user");
                        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(USER_KEY, userJson.toString());
                        editor.putString("user_photo", jsonResponse.getString("photo"));
                        editor.apply();

                        Intent intent = new Intent(context, HomePage.class);
                        context.startActivity(intent);
                    }
                } else {
                    Log.i("SignInManager", "POST request did not work. Response Code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("SignInManager", "POST request failed: " + e.getLocalizedMessage(), e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    public boolean isSignedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(USER_KEY);
    }

    public void signOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public JSONObject getSignedInUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString(USER_KEY, null);
        if (userJsonString != null) {
            try {
                return new JSONObject(userJsonString);
            } catch (Exception e) {
                Log.e("SignInService", "Failed to parse user JSON", e);
            }
        }
        return null;
    }

    public String getSignedInUserPhoto() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_photo", null);
    }
}