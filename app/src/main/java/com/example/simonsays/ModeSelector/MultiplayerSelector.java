package com.example.simonsays.ModeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.R;
import com.example.simonsays.Simon.SimonActivity;
import com.example.simonsays.Simon.SimonMultiplayer;

public class MultiplayerSelector extends AppCompatActivity {
    private static final int REQUEST_CODE = 111;
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
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void unirseSala() {
        Intent intent = new Intent(MultiplayerSelector.this, SimonMultiplayer.class);
        intent.putExtra("userNumber", 2);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                int result = data.getIntExtra("result_key", 0);
                if (result==1)
                    Toast.makeText(MultiplayerSelector.this, "YOU WIN!! Congratilations", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "YOU LOOSE!!", Toast.LENGTH_SHORT).show();
            }
        }
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
