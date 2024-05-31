package com.example.simonsays.ModeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simonsays.Music.AudioService;
import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.R;
import com.example.simonsays.Simon.SimonActivity;
import com.example.simonsays.Simon.SimonMultiplayer;
import com.example.simonsays.databinding.MultiplayerSelectorBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MultiplayerSelector extends AppCompatActivity {
    MultiplayerSelectorBinding binding;
    private static final int REQUEST_CODE = 111;
    private DatabaseReference mDatabaseSalas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MultiplayerSelectorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        binding.noSala.setVisibility(View.INVISIBLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDatabaseSalas = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas");

        binding.crearSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearSala();
            }
        });

        binding.unirse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unirseSala();
            }
        });
    }

    private void crearSala() {
        binding.noSala.setVisibility(View.INVISIBLE);
        mDatabaseSalas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el número de salas existentes
                int salaCount = (int) dataSnapshot.getChildrenCount();

                // Generar el nombre de la nueva sala
                String newSalaName = "Sala" + (salaCount + 1);

                // Crear los campos para la nueva sala
                Map<String, Object> newSalaData = new HashMap<>();
                Random random = new Random();
                newSalaData.put("Next", random.nextInt(4));
                newSalaData.put("Turn", 0);
                newSalaData.put("User1", "");
                newSalaData.put("User2", "");

                // Añadir la nueva sala a la base de datos
                mDatabaseSalas.child(newSalaName).setValue(newSalaData)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("CreateSala", "Successful");
                                Intent intent = new Intent(MultiplayerSelector.this, SimonMultiplayer.class);
                                intent.putExtra("userNumber", 1);
                                intent.putExtra("salaNumber", (salaCount + 1));
                                startActivityForResult(intent, REQUEST_CODE);
                            } else {
                                Log.d("CreateSala", "Not Created");
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("CreateSala", "Cancelled");
            }
        });

    }

    private void unirseSala() {
        binding.noSala.setVisibility(View.VISIBLE);
        mDatabaseSalas.addListenerForSingleValueEvent(new ValueEventListener() {
            int aux=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot salaSnapshot : dataSnapshot.getChildren()) {
                    String user2 = salaSnapshot.child("User2").getValue(String.class);
                    if (user2 != null && user2.isEmpty()) {
                        String salaName = salaSnapshot.getKey();
                        Log.d("Sala", "La primera sala con User2 en blanco es: " + salaName);
                        char lastChar = salaName.charAt(salaName.length() - 1);
                        Log.d("PRUEBA", String.valueOf(lastChar));
                        if (Character.isDigit(lastChar)) {
                            if (aux==0){
                                aux=1;
                                Intent intent = new Intent(MultiplayerSelector.this, SimonMultiplayer.class);
                                intent.putExtra("userNumber", 2);
                                intent.putExtra("salaNumber", Integer.valueOf(String.valueOf(lastChar)));
                                startActivityForResult(intent,REQUEST_CODE);
                                binding.noSala.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            // Maneja el caso en el que el último carácter no sea un dígito
                            throw new NumberFormatException("El último carácter del nombre de la sala no es un dígito.");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al leer la base de datos: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                int result = data.getIntExtra("result_key", 0);
                String salaName = data.getStringExtra("result_sala");
                if (result==1){
                    Toast.makeText(MultiplayerSelector.this, R.string.you_win_congratilations, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, R.string.you_loose, Toast.LENGTH_SHORT).show();
                }
                mDatabaseSalas.child(salaName).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.e("DeleteSala", "Error al eliminar la sala: " + databaseError.getMessage());
                        } else {
                            Log.d("DeleteSala", "Sala eliminada con éxito: " + salaName);
                        }
                    }
                });
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
