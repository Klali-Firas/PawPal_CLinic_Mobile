package com.example.pawpalclinic.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pawpalclinic.R;
import com.example.pawpalclinic.service.SignInService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

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
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private boolean doubleBackToExitPressedOnce = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable resetDoubleBackFlag = () -> doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);

        // Charger l'URL de la photo de profil et le nom de l'utilisateur à partir de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        String imageUrl = sharedPreferences.getString(USER_PHOTO_KEY, null);
        JSONObject user = new SignInService(getApplicationContext()).getSignedInUser();
        String userName = user.optString("nom") + " " + user.optString("prenom");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            new LoadProfileImageTask(toolbar, navigationView).execute(imageUrl);
            new LoadProfileImageDrawableTask(profileImageView).execute(imageUrl);
        } else {
            // Définir une image par défaut si aucune URL valide n'est trouvée
            profileImageView.setImageResource(R.drawable.ic_profile_placeholder);
        }

        userNameTextView.setText(userName);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_ai_assistant) {
                Intent intent = new Intent(this, AI_Assistant.class);
                startActivity(intent);
            } else if (itemId == R.id.nav_logout) {
                new SignInService(getApplicationContext()).signOut();
            } else if (itemId == R.id.aprops) {
                Intent intent = new Intent(this, apropos.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            // Ne rien faire lors de la resélection
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

        // Définir le fragment par défaut
        if (savedInstanceState == null) {
            currentFragment = AnimauxFragment.newInstance(1);
            fragmentMap.put(R.id.nav_animaux, currentFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, currentFragment)
                    .commit();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish();
                } else {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(HomePage.this, "Cliquez à nouveau pour quitter", Toast.LENGTH_SHORT).show();
                    handler.postDelayed(resetDoubleBackFlag, 2000);
                }
            }
        });
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

    private static class LoadProfileImageDrawableTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        LoadProfileImageDrawableTask(ImageView imageView) {
            this.imageView = imageView;
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
                imageView.setImageBitmap(circularBitmap);
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