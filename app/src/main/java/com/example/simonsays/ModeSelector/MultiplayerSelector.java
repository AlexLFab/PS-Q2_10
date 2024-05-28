package com.example.simonsays.ModeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.R;
import com.example.simonsays.Simon.SimonActivity;
import com.example.simonsays.Simon.SimonMultiplayer;

public class MultiplayerSelector extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_selector);

        Button crearSalaButton = findViewById(R.id.crearSala);
        Button unirseButton = findViewById(R.id.unirse);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        crearSalaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearSala();
            }
        });

        unirseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirseSala();
            }
        });
    }

    private void crearSala() {
        Intent intent = new Intent(MultiplayerSelector.this, SimonMultiplayer.class);
        intent.putExtra("userNumber", 1);
        startActivity(intent);
    }

    private void unirseSala() {
        Intent intent = new Intent(MultiplayerSelector.this, SimonMultiplayer.class);
        intent.putExtra("userNumber", 2);
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
}
