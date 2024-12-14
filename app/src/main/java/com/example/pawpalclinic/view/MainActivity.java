// MainActivity.java
package com.example.pawpalclinic.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
                Log.i("MainActivity", "Activity result received.");
                signInService.handleSignInResult(result.getResultCode(), result.getData());
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInService = new SignInService(this, signInLauncher);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signInService.signIn());

        Button navButton = (Button)findViewById(R.id.navButton);
        navButton.setOnClickListener(v -> {
            Log.i("MainActivity", "Navigating to Home Page");
            startActivity(new Intent(MainActivity.this, HomePage.class));
        });

    }


}