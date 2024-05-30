package com.example.simonsays.ModeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simonsays.Music.AudioService;
import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.R;
import com.example.simonsays.Simon.SimonActivity;
import com.example.simonsays.Simon.SimonMultiplayer;
import com.example.simonsays.StartActivity;

public class ModeSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_selector);

        Button singlePlayerButton = findViewById(R.id.singlePlayerButton);
        Button multiPlayerButton = findViewById(R.id.multiPlayerButton);
        Button reflexTestButton = findViewById(R.id.reflexTest);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        singlePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singlePlayer();
            }
        });

        multiPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiPlayer();
            }
        });

        reflexTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reflexTest();
            }
        });
    }

    private void singlePlayer() {
        Intent intent = new Intent(ModeSelectorActivity.this, SimonActivity.class);
        startActivity(intent);
    }

    private void multiPlayer() {
        Intent intent = new Intent(ModeSelectorActivity.this, MultiplayerSelector.class);
        startActivity(intent);
    }

    private void reflexTest() {
        Intent intent = new Intent(ModeSelectorActivity.this, PianoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        Intent i = new Intent(this, AudioService.class);
        i.putExtra("action", AudioService.START);
        startService(i);
    }
}
