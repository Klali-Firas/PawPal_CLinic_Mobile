package com.example.pawpalclinic.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.model.Animaux;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class EditAnimal extends AppCompatActivity {

    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_KEY = "user";
    private EditText nameInput, raceInput, ageInput;
    private AnimauxController animauxController;
    private Animaux currentAnimal;
    private int proprietaireId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        nameInput = findViewById(R.id.animal_name_input);
        raceInput = findViewById(R.id.animal_race_input);
        ageInput = findViewById(R.id.animal_age_input);
        Button saveButton = findViewById(R.id.save_button);

        animauxController = new AnimauxController(this);

        // Retrieve the current authenticated user's ID
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String userJsonString = sharedPreferences.getString(USER_KEY, null);
        if (userJsonString != null) {
            try {
                JSONObject userJson = new JSONObject(userJsonString);
                proprietaireId = userJson.getInt("id");
            } catch (Exception e) {
                Toast.makeText(this, "Error retrieving user ID", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = getIntent();
        if (intent.hasExtra("animaux_json")) {
            // Update existing animal
            String animauxJson = intent.getStringExtra("animaux_json");
            currentAnimal = new Gson().fromJson(animauxJson, Animaux.class);
            populateFields(currentAnimal);
        } else {
            // Add new animal
            currentAnimal = null;
        }

        saveButton.setOnClickListener(v -> saveAnimal());
    }

    private void populateFields(Animaux animal) {
        nameInput.setText(animal.getNom());
        raceInput.setText(animal.getRace());
        ageInput.setText(String.valueOf(animal.getAge()));
    }

    private void saveAnimal() {
        String name = nameInput.getText().toString();
        String race = raceInput.getText().toString();
        int age = Integer.parseInt(ageInput.getText().toString());

        if (currentAnimal == null) {
            // Add new animal
            Animaux newAnimal = new Animaux(0, proprietaireId, name, race, age, new Date());
            CompletableFuture<Animaux> future = animauxController.createAnimaux(newAnimal);
            future.thenAccept(animal -> runOnUiThread(() -> {
                Toast.makeText(this, "Animal added successfully", Toast.LENGTH_SHORT).show();
                finish();
            })).exceptionally(throwable -> {
                runOnUiThread(() -> Toast.makeText(this, "Error adding animal: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
                return null;
            });
        } else {
            // Update existing animal
            currentAnimal.setNom(name);
            currentAnimal.setRace(race);
            currentAnimal.setAge(age);
            CompletableFuture<Animaux> future = animauxController.updateAnimaux(currentAnimal.getId(), currentAnimal);
            future.thenAccept(animal -> runOnUiThread(() -> {
                Toast.makeText(this, "Animal updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            })).exceptionally(throwable -> {
                runOnUiThread(() -> Toast.makeText(this, "Error updating animal: " + throwable.getMessage(), Toast.LENGTH_SHORT).show());
                return null;
            });
        }
    }
}