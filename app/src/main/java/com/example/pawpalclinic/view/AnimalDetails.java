package com.example.pawpalclinic.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.model.Animaux;
import com.google.gson.Gson;

public class AnimalDetails extends AppCompatActivity {

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_animal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the animal details from the intent
        String animauxJson = getIntent().getStringExtra("animaux_json");
        Animaux animaux = gson.fromJson(animauxJson, Animaux.class);

        // Set the animal details to the views
        TextView nameTextView = findViewById(R.id.animal_name);
        TextView raceTextView = findViewById(R.id.animal_race);
        TextView ageTextView = findViewById(R.id.animal_age);

        nameTextView.setText(animaux.getNom());
        raceTextView.setText(animaux.getRace());
        ageTextView.setText(String.valueOf(animaux.getAge()));
    }
}