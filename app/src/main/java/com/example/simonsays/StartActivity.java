package com.example.simonsays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

import com.example.simonsays.ModeSelector.ModeSelectorActivity;
import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.Simon.SimonActivity;

public class StartActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Boolean isLogged = false;
    private FirebaseAuth mAuth;

    private MusicViewModel musicViewModel;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        applyLanguage();

        setContentView(R.layout.activity_start);



        Button startButton = findViewById(R.id.startButton);
        Button menuButton = findViewById(R.id.menuButton);
        Button logInButton = findViewById(R.id.logInButton);
        Button leaderboardsButton = findViewById(R.id.leaderboards);
        TextView textView = findViewById(R.id.usernameDisplay);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");


        // Obtener las preferencias y aplicar el modo nocturno si está activado
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        // Obtener el ViewModel
        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        // Configurar el volumen inicial basado en la preferencia
        int volume = sharedPreferences.getInt("music_volume", 50);
        setMusicVolume(volume);

        // Registrar un listener para cambios en las preferencias
        sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
            if (key.equals("music_volume")) {
                int newVolume = prefs.getInt(key, 50);
                setMusicVolume(newVolume);
            }
            if (key.equals("night_mode")) {
                recreate();
            }
            if (key.equals("language_preference")) {
                applyLanguage();
                recreate();
            }
        });

        // Iniciar la reproducción de la música
        if (!musicViewModel.getMediaPlayer().isPlaying()) {
            musicViewModel.getMediaPlayer().start();
        }

        if(mAuth.getUid() != null && isNetworkAvailable()) {


            mDatabase.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    String username = userSnapshot.child("username").getValue(String.class);
                                    textView.setText(username);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(StartActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            textView.setText("Guest");
                        }
                    });
        }else{
            textView.setText("Guest");
        }
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuGame();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }


        });
        leaderboardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaderboards();
            }
        });
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

    private void setMusicVolume(int volume) {
        float volumeLevel = volume / 100.0f;
        if (musicViewModel.getMediaPlayer() != null) {
            musicViewModel.getMediaPlayer().setVolume(volumeLevel, volumeLevel);
        }
    }


    private void startGame() {
        Intent intent = new Intent(StartActivity.this, ModeSelectorActivity.class);
        startActivity(intent);
    }

    private void menuGame() {
        Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void leaderboards(){
        Intent intent = new Intent(StartActivity.this, Leaderboards.class);
        startActivity(intent);
    }

    private void logIn() {
        Intent intent = new Intent(StartActivity.this, Login.class);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}