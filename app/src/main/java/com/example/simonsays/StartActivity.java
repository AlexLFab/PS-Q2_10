package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Boolean isLogged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);
        Button menuButton = findViewById(R.id.menuButton);
        Button logInButton = findViewById(R.id.logInButton);
        Button leaderboardsButton = findViewById(R.id.leaderboards);

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
    }

    private void startGame() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finaliza la actividad de inicio para que no pueda volver atrás
    }

    private void menuGame() {

    }

    private void logIn() {
        Intent intent = new Intent(StartActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}