package com.example.simonsays;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simonsays.Music.MusicViewModel;
import com.example.simonsays.Music.AudioService;
import com.example.simonsays.databinding.ActivityStartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.example.simonsays.ModeSelector.ModeSelectorActivity;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        applyLanguage();

        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        PeriodicWorkRequest leaderboardWorkRequest =
                new PeriodicWorkRequest.Builder(LeaderboardWorker.class, 15, TimeUnit.MINUTES)
                        .build();
        WorkManager.getInstance(this).enqueue(leaderboardWorkRequest);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");

            // Obtener las preferencias y aplicar el modo nocturno si está activado
            boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }


        if(mAuth.getUid() != null && isNetworkAvailable()) {

            mDatabase.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    String username = userSnapshot.child("username").getValue(String.class);
                                    binding.usernameDisplay.setText(username);


                                    // Guardar el nombre de usuario en SharedPreferences
                                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("current_user", username);
                                    editor.apply();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(StartActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            binding.usernameDisplay.setText("Guest");
                        }
                    });
        }else{
            binding.usernameDisplay.setText("Guest");
        }

        binding.startButton.setOnClickListener(this::startGame);
        binding.menuButton.setOnClickListener(this::menuGame);
        binding.logInButton.setOnClickListener(this::logIn);
        binding.leaderboards.setOnClickListener(this::leaderboards);

    }


    private void adjustMusicVolume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        float volume = 0.5f; // Valor predeterminado

        try {
            volume = prefs.getFloat("music_volume", 0.5f);  // Intenta obtener el valor como flotante
        } catch (ClassCastException e) {
            // Si hay una excepción de conversión, intenta obtenerlo como entero y conviértelo a flotante
            int volumeInt = prefs.getInt("music_volume", 50); // Valor predeterminado en porcentaje
            volume = volumeInt / 100.0f; // Convertir a escala de 0 a 1
        }

        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.SET_VOLUME);
        i.putExtra("volume", volume);
        startService(i);
    }
    @Override
    public void onPause() {
        super.onPause();

        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.PAUSE);
        startService(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAuth.getUid() != null && isNetworkAvailable()) {

            mDatabase.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    String username = userSnapshot.child("username").getValue(String.class);
                                    binding.usernameDisplay.setText(username);


                                    // Guardar el nombre de usuario en SharedPreferences
                                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("current_user", username);
                                    editor.apply();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(StartActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            binding.usernameDisplay.setText("Guest");
                        }
                    });
        }else{
            binding.usernameDisplay.setText("Guest");
        }
        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.START);
        startService(i);
        adjustMusicVolume();
    }



    private void applyLanguage() {
        String language = sharedPreferences.getString("language_preference", "");
        Log.d("Macedonia", "applyLanguage: " + language); // Agregar log para verificar el idioma
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);

        Context context = createConfigurationContext(config);
        res.updateConfiguration(config, context.getResources().getDisplayMetrics());

        // Agrega este log para verificar la configuración
        Log.d("Macedonia", "Idioma aplicado: " + config.locale.getLanguage());
    }

    // Método para inicializar o actualizar el servicio de música con el volumen proporcionado



    private void startGame(View view) {
        Intent intent = new Intent(StartActivity.this, ModeSelectorActivity.class);
        startActivity(intent);
    }

    private void menuGame(View view) {
        Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void leaderboards(View view){
        Intent intent = new Intent(StartActivity.this, Leaderboards.class);
        startActivity(intent);
    }

    private void logIn(View view) {
        Intent intent = new Intent(StartActivity.this, Login.class);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}