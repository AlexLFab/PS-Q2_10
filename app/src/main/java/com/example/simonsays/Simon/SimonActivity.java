package com.example.simonsays.Simon;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.simonsays.R;
import com.example.simonsays.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SimonActivity extends AppCompatActivity {

    private ArrayList<Integer> sequence = new ArrayList<>();
    private ArrayList<Integer> playerInput = new ArrayList<>();
    private TextView countdownTextView;
    private Button greenButton, redButton, blueButton, yellowButton;
    private Button b_play;
    private TextView tv_score, tv_beat;
    private int currentIndex = 0;
    private int sequenceLength;

    private int score;
    private int highscore = 0;

    private boolean player_turn = false;

    private Handler handler = new Handler();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon);

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
        tv_score.setText(getString(R.string.score_label) +score);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");


        if(mAuth.getUid() != null && isNetworkAvailable()) {


            mDatabase.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    int record = userSnapshot.child("record").getValue(Integer.class);
                                    tv_beat.setText(getString(R.string.best)+record);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(SimonActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            tv_beat.setText(getString(R.string.best) + highscore);
                        }
                    });
        }else{
            tv_beat.setText(getString(R.string.best) + highscore);
        }

        // Set button listeners
        greenButton.setOnClickListener(v -> onColorButtonClicked(0));
        redButton.setOnClickListener(v -> onColorButtonClicked(1));
        blueButton.setOnClickListener(v -> onColorButtonClicked(2));
        yellowButton.setOnClickListener(v -> onColorButtonClicked(3));
        disableButtons();

        b_play.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        score = 0;
        sequenceLength = 1;
        b_play.setVisibility(View.INVISIBLE);
        playerInput.clear();
        sequence.clear();
        generateSequence();
        startCountdown();
    }

    private void startCountdown() {
        countdownTextView.setText("3");
        handler.postDelayed(() -> {
            countdownTextView.setText("2");
            handler.postDelayed(() -> {
                countdownTextView.setText("1");
                handler.postDelayed(() -> {
                    if (player_turn) {
                        countdownTextView.setText("Your Turn!");
                        enableButtons();
                    } else {
                        countdownTextView.setText("Pay Attention!");
                        showSequence();
                    }
                }, 1000);
            }, 1000);
        }, 1000);
    }

    private void generateSequence() {
        //sequence.clear(); // Clear the old sequence
        Random random = new Random();
        for (int i = 0; i < sequenceLength; i++) {
            int next = random.nextInt(4);
            sequence.add(next); // 0: green, 1: red, 2: blue, 3: yellow
            Log.d("Color Sequence: ", String.valueOf(sequence));
        }
    }

    private void disableButtons() {
        greenButton.setEnabled(false);
        redButton.setEnabled(false);
        blueButton.setEnabled(false);
        yellowButton.setEnabled(false);
    }

    private void enableButtons() {
        greenButton.setEnabled(true);
        redButton.setEnabled(true);
        blueButton.setEnabled(true);
        yellowButton.setEnabled(true);
    }

    private void showSequence() {
        disableButtons(); // Desactivar los botones al inicio de la secuencia

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sequence.size(); i++) {
                    final int colorIndex = sequence.get(i);
                    final int finalI = i; // Variable final local
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            highlightButton(colorIndex);
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    unhighlightButtons();
                                }
                            }, 1500); // Des-resalta el botón después de 1.5 segundos
                            if (finalI == sequence.size() - 1) {
                                player_turn = true;
                                startCountdown();
                                enableButtons(); // Activar los botones al final de la secuencia
                            }
                        }
                    }, finalI * 2000); // Resalta cada botón con un retraso de 2 segundos
                }
            }
        }, 500);
    }

    private void highlightButton(int colorIndex) {
        int highlightColor;
        switch (colorIndex) {
            case 0:
                highlightColor = ContextCompat.getColor(this, R.color.highlight_green);
                greenButton.setBackgroundColor(highlightColor);
                break;
            case 1:
                highlightColor = ContextCompat.getColor(this, R.color.highlight_red);
                redButton.setBackgroundColor(highlightColor);
                break;
            case 2:
                highlightColor = ContextCompat.getColor(this, R.color.highlight_blue);
                blueButton.setBackgroundColor(highlightColor);
                break;
            case 3:
                highlightColor = ContextCompat.getColor(this, R.color.highlight_yellow);
                yellowButton.setBackgroundColor(highlightColor);
                break;
        }
    }

    private void unhighlightButtons() {
        greenButton.setBackgroundColor(ContextCompat.getColor(this, R.color.normal_green));
        redButton.setBackgroundColor(ContextCompat.getColor(this, R.color.normal_red));
        blueButton.setBackgroundColor(ContextCompat.getColor(this, R.color.normal_blue));
        yellowButton.setBackgroundColor(ContextCompat.getColor(this, R.color.normal_yellow));
    }

    private void onColorButtonClicked(int colorIndex) {
        playerInput.add(colorIndex);
        if (playerInput.get(currentIndex) == sequence.get(currentIndex)) {
            currentIndex++;
            if (currentIndex == sequence.size()) {
                currentIndex = 0;
                playerInput.clear();
                score++;
                tv_score.setText(getString(R.string.score_label) + score);
                generateSequence();
                player_turn = false;
                startCountdown();
                updateHighscore(score);
            }
        } else {
            Toast.makeText(this, "Error! Try again.", Toast.LENGTH_SHORT).show();
            currentIndex = 0;
            playerInput.clear();
            score = 0;
            tv_score.setText(getString(R.string.score_label) + score);
            sequenceLength=1;
            sequence.clear();
            generateSequence();
            player_turn = false;
            startCountdown();

        }
    }

    private void updateHighscore(int score){
        Log.d("Update", "Entra en el update");

        if(mAuth.getUid() != null && isNetworkAvailable()) {


            mDatabase.orderByChild("uid").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                                    int record = userSnapshot.child("record").getValue(Integer.class);
                                    if (score>record){
                                        Log.d("Update", "La score es mayor que el record del user");
                                        String key = userSnapshot.getKey();
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("record", score);

                                        mDatabase.child(key).updateChildren(map);

                                        highscore = score;
                                        tv_beat.setText(getString(R.string.best) + highscore);
                                        SharedPreferences preferences = getSharedPreferences("PREF", 0);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt("highSimonscore", record);
                                        editor.apply();

                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error en la consulta
                            Toast.makeText(SimonActivity.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            tv_beat.setText(getString(R.string.best) + highscore);
                        }
                    });
        }else{
            if (score>highscore) {
                highscore = score;
                tv_beat.setText(getString(R.string.best) + highscore);
                SharedPreferences preferences = getSharedPreferences("PREF", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("highSimonscore", highscore);
                editor.apply();
            }
        }

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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