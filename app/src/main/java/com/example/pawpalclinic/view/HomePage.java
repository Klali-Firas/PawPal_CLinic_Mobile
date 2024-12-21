package com.example.pawpalclinic.view;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.BitmapShader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.pawpalclinic.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    private static final String SHARED_PREFS_NAME = "user_prefs";
    private static final String USER_PHOTO_KEY = "user_photo";
    private DrawerLayout drawerLayout;
    private Fragment currentFragment = null;
    private Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load profile picture URL from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString(USER_PHOTO_KEY, null);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            new LoadProfileImageTask(toolbar, navigationView).execute(imageUrl);
        } else {
            // Set a default image if no valid URL is found
            toolbar.setNavigationIcon(R.drawable.ic_profile_placeholder);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                // Handle home action
            } else if (itemId == R.id.nav_profile) {
                // Handle profile action
            } else if (itemId == R.id.nav_settings) {
                // Handle settings action
            }
            drawerLayout.closeDrawers();
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            // Do nothing when reselected
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment == null) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_animaux) {
                    selectedFragment = AnimauxFragment.newInstance(1);
                } else if (itemId == R.id.nav_rendezvous) {
                    selectedFragment = RendezVousFragment.newInstance(1);
                } else if (itemId == R.id.nav_add) {
                    selectedFragment = new AddRendezVousFragment();
                } else if (itemId == R.id.nav_shop) {
                    selectedFragment = ProduitFragment.newInstance();
                } else if (itemId == R.id.nav_commandes) {
                    selectedFragment = new CommandeFragment();
                }
                fragmentMap.put(itemId, selectedFragment);
            }

            if (selectedFragment != null && selectedFragment != currentFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (selectedFragment.isAdded()) {
                    transaction.hide(currentFragment).show(selectedFragment);
                } else {
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                    transaction.add(R.id.fragment_container, selectedFragment);
                }
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                currentFragment = selectedFragment;
            }

            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            currentFragment = AnimauxFragment.newInstance(1);
            fragmentMap.put(R.id.nav_animaux, currentFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, currentFragment)
                    .commit();
        }
    }

    private static class LoadProfileImageTask extends AsyncTask<String, Void, Bitmap> {
        private final Toolbar toolbar;
        private final NavigationView navigationView;

        LoadProfileImageTask(Toolbar toolbar, NavigationView navigationView) {
            this.toolbar = toolbar;
            this.navigationView = navigationView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                ImageView profileImageView = new ImageView(toolbar.getContext());
                profileImageView.setImageBitmap(circularBitmap);
                profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                profileImageView.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT));
                toolbar.setNavigationIcon(profileImageView.getDrawable());
                toolbar.setNavigationOnClickListener(v -> ((DrawerLayout) toolbar.getParent().getParent()).openDrawer(navigationView));
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_profile_placeholder);
            }
        }

        private Bitmap getCircularBitmap(Bitmap bitmap) {
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float radius = size / 2f;
            canvas.drawCircle(radius, radius, radius, paint);

            return output;
        }
    }
}