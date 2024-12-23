package com.example.pawpalclinic.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.controller.AnimauxController;
import com.example.pawpalclinic.controller.RendezVousController;
import com.example.pawpalclinic.controller.ServiceController;
import com.example.pawpalclinic.model.Animaux;
import com.example.pawpalclinic.model.RendezVous;
import com.example.pawpalclinic.model.Service;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AnimalDetails extends AppCompatActivity {
    private Animaux animaux;

    private static final String TAG = "DétailsAnimal";
    private Gson gson = new Gson();
    private RendezVousController rendezVousController;
    private ServiceController serviceController;
    private LinearLayout rendezvousContainer;
    private AnimauxController animauxController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animauxController = new AnimauxController(this);

        rendezVousController = new RendezVousController(this);
        serviceController = new ServiceController(this);
        rendezvousContainer = findViewById(R.id.rendezvous_container);

        // Obtenir les détails de l'animal à partir de l'intention
        String animauxJson = getIntent().getStringExtra("animaux_json");
        animaux = gson.fromJson(animauxJson, Animaux.class);



        // Récupérer et afficher l'historique des rendez-vous

        ImageButton editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(AnimalDetails.this, EditAnimal.class);
            intent.putExtra("animaux_json", gson.toJson(animaux));
            startActivity(intent);
        });
    }



    private void fetchAndDisplayRendezvousHistory(int animalId) {
        rendezvousContainer.removeAllViews();
        Log.d(TAG, "Récupération de l'historique des rendez-vous pour l'ID de l'animal : " + animalId);
        rendezVousController.getAllRendezVous().thenAccept(rendezVousList -> {
            List<RendezVous> filteredRendezVousList = rendezVousList.stream()
                    .filter(rendezVous -> rendezVous.getAnimalId() == animalId)
                    .collect(Collectors.toList());

            if (!filteredRendezVousList.isEmpty()) {
                Log.d(TAG, "Taille de la liste des rendez-vous filtrés : " + filteredRendezVousList.size());
                for (RendezVous rendezVous : filteredRendezVousList) {
                    serviceController.getServiceById(rendezVous.getMotif()).thenAccept(service -> {
                        runOnUiThread(() -> addRendezvousToView(rendezVous, service));
                    });
                }
            } else {
                Log.d(TAG, "Aucun rendez-vous trouvé pour l'ID de l'animal : " + animalId);
            }
        }).exceptionally(throwable -> {
            Log.e(TAG, "Erreur lors de la récupération de l'historique des rendez-vous", throwable);
            return null;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAnimalDetails();
    }

    private void loadAnimalDetails() {
        animauxController.getAnimauxById(animaux.getId()).thenAccept(a -> {
            animaux =  a; // Mettre à jour la variable locale
            runOnUiThread(() -> {
                // Définir les détails de l'animal aux vues
                TextView nameTextView = findViewById(R.id.animal_name);
                TextView raceTextView = findViewById(R.id.animal_race);
                TextView ageTextView = findViewById(R.id.animal_age);

                nameTextView.setText(animaux.getNom());
                raceTextView.setText(animaux.getRace());
                ageTextView.setText(String.valueOf(animaux.getAge()) + " mois");

                // Récupérer et afficher l'historique des rendez-vous
                fetchAndDisplayRendezvousHistory(animaux.getId());
            });
        }).exceptionally(throwable -> {
            Log.e(TAG, "Erreur lors du chargement des détails de l'animal", throwable);
            return null;
        });
    }

    private void addRendezvousToView(RendezVous rendezVous, Service service) {
        MaterialCardView cardView = new MaterialCardView(this);
        cardView.setCardElevation(6);
        cardView.setStrokeWidth(0);
        cardView.setPadding(16, 16, 16, 16);
        cardView.setUseCompatPadding(true);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.baseline_pets_24); // Remplacer par votre ressource d'image
        imageView.setColorFilter(getResources().getColor(R.color.md_theme_tertiary, getTheme()));
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        imageView.setLayoutParams(imageParams);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                3
        );
        textLayout.setLayoutParams(textLayoutParams);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy hh:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(rendezVous.getDateRendezVous());

        TextView dateView = new TextView(this);
        dateView.setText(formattedDate);
        dateView.setPadding(8, 8, 8, 8);

        Chip serviceChip = new Chip(this);
        serviceChip.setText(service.getNomService());
        serviceChip.setChipBackgroundColorResource(R.color.md_theme_primaryContainer);
        serviceChip.setTextColor(getResources().getColor(R.color.md_theme_onPrimaryContainer, getTheme()));
        serviceChip.setTextSize(12); // Taille de texte plus petite
        serviceChip.setChipCornerRadius(50); // Rayon de bordure complet
        serviceChip.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        serviceChip.setLayoutParams(chipParams);
        serviceChip.setChipStrokeColorResource(R.color.md_theme_onPrimaryContainer);
        serviceChip.setChipStrokeWidth(1);

        Chip statusChip = new Chip(this);
        statusChip.setText(rendezVous.getStatut());
        statusChip.setChipBackgroundColorResource(R.color.md_theme_tertiaryContainer);
        statusChip.setTextColor(getResources().getColor(R.color.md_theme_onTertiaryContainer, getTheme()));
        statusChip.setTextSize(12); // Taille de texte plus petite
        statusChip.setChipCornerRadius(50); // Rayon de bordure complet
        statusChip.setPadding(8, 8, 8, 8);
        statusChip.setLayoutParams(chipParams);
        statusChip.setChipStrokeColorResource(R.color.md_theme_onTertiaryContainer);
        statusChip.setChipStrokeWidth(1);

        textLayout.addView(dateView);
        textLayout.addView(serviceChip);
        textLayout.addView(statusChip);

        headerLayout.addView(imageView);
        headerLayout.addView(textLayout);

        cardContent.addView(headerLayout);

        if (rendezVous.getRemarques() != null && !rendezVous.getRemarques().isEmpty()) {
            TextView remarksView = new TextView(this);
            remarksView.setText("Remarques : " + rendezVous.getRemarques());
            remarksView.setPadding(8, 8, 8, 8);
            cardContent.addView(remarksView);
        }

        cardView.addView(cardContent);
        rendezvousContainer.addView(cardView);
    }
}