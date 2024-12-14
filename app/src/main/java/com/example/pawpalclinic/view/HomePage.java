package com.example.pawpalclinic.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pawpalclinic.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_animaux) {
                selectedFragment = AnimauxFragment.newInstance(1);
            } else if (item.getItemId() == R.id.nav_rendezvous) {
                selectedFragment = RendezVousFragment.newInstance(1);
            } else if (item.getItemId() == R.id.nav_add) {
                selectedFragment = RendezVousFragment.newInstance(1);
                // Handle add item
            } else if (item.getItemId() == R.id.nav_shop) {
                // Handle shop item
            } else if (item.getItemId() == R.id.nav_commandes) {
                // Handle commandes item
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }

            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, AnimauxFragment.newInstance(1))
                    .commit();
        }
    }
}