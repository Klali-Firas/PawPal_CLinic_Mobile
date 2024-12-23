// MainActivity.java
package com.example.pawpalclinic.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.service.SignInService;
import com.google.android.gms.common.SignInButton;

public class MainActivity extends AppCompatActivity {
    private SignInService signInService;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                Log.i("MainActivity", "Résultat de l'activité reçu.");
                signInService.handleSignInResult(result.getResultCode(), result.getData());
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInService = new SignInService(this, signInLauncher);

        if (signInService.isSignedIn()) {
            startActivity(new Intent(this, HomePage.class));
            finish();
            return;
        }

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signInService.signIn());
    }
}