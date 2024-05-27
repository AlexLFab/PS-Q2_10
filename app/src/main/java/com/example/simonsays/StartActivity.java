package com.example.simonsays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import com.example.simonsays.ModeSelector.ModeSelectorActivity;
import com.example.simonsays.Piano.PianoActivity;
import com.example.simonsays.Simon.SimonActivity;

public class StartActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Boolean isLogged = false;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.startButton);
        Button menuButton = findViewById(R.id.menuButton);
        Button logInButton = findViewById(R.id.logInButton);
        Button leaderboardsButton = findViewById(R.id.leaderboards);
        TextView textView = findViewById(R.id.usernameDisplay);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");


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

    private void startGame() {
        Intent intent = new Intent(StartActivity.this, ModeSelectorActivity.class);
        startActivity(intent);
    }

    private void menuGame() {
        //TODO
    }

    private void leaderboards(){
        Intent intent = new Intent(StartActivity.this, Leaderboards.class);
        startActivity(intent);
    }

    private void logIn() {
        Intent intent = new Intent(StartActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}