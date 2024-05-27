package com.example.simonsays.Simon;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simonsays.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SimonMultiplayer extends AppCompatActivity {
    private Button greenButton, redButton, blueButton, yellowButton;
    private Button b_play;
    private TextView tv_score, tv_beat;
    private TextView countdownTextView;
    private int score;
    private int highscore = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseSala;
    private ArrayList<Integer> sequence = new ArrayList<>();
    private int userNumber;
    private int turnSala;
    int nextSala;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        // Initialize buttons and text views
        greenButton = findViewById(R.id.greenButton);
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        yellowButton = findViewById(R.id.yellowButton);
        b_play = findViewById(R.id.b_play);
        countdownTextView = findViewById(R.id.countdownTextView);
        tv_score = findViewById(R.id.tv_score);
        tv_beat = findViewById(R.id.tv_beat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Set initial values
        SharedPreferences preferences = getSharedPreferences("PREF", 0);
        highscore = preferences.getInt("highSimonscore", 0);
        tv_score.setText(getString(R.string.score_label, score));
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");
        mDatabaseSala = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas/Sala1");

        if(mAuth.getUid() != null && isNetworkAvailable()) {


            mDatabaseUsers.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    int record = userSnapshot.child("record").getValue(Integer.class);
                                    tv_beat.setText(getString(R.string.best, record));

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(SimonMultiplayer.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            tv_beat.setText(getString(R.string.best, highscore));
                        }
                    });

            mDatabaseSala
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                String user1 = dataSnapshot.child("User1").getValue(String.class);
                                String user2 = dataSnapshot.child("User2").getValue(String.class);
                                turnSala = dataSnapshot.child("Turn").getValue(Integer.class);
                                nextSala = dataSnapshot.child("Next").getValue(Integer.class);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //TODO
                            //Deletear Sala de la base de datos
                            finish();
                        }
                    });
        }else{
            finish();
        }

        // Set button listeners
        /*greenButton.setOnClickListener(v -> onColorButtonClicked(0));
        redButton.setOnClickListener(v -> onColorButtonClicked(1));
        blueButton.setOnClickListener(v -> onColorButtonClicked(2));
        yellowButton.setOnClickListener(v -> onColorButtonClicked(3));
        disableButtons();

        b_play.setOnClickListener(v -> startGame());*/
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
