package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.Simon.SimonActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);
        Button menuButton = findViewById(R.id.menuButton);
        Button tutorialButton = findViewById(R.id.tutorialButton);

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

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialGame();
            }
        });
    }

    private void startGame() {
        Intent intent = new Intent(StartActivity.this, SimonActivity.class);
        startActivity(intent);
        finish(); // Finaliza la actividad de inicio para que no pueda volver atrás
    }

    private void menuGame() {

    }

    private void tutorialGame() {
        Intent intent = new Intent(StartActivity.this, PianoActivity.class);
        startActivity(intent);
        finish();
    }
}