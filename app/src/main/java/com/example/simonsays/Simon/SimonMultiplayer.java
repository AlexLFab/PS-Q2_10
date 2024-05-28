package com.example.simonsays.Simon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.simonsays.databaseItems.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.example.simonsays.databaseItems.User;

public class SimonMultiplayer extends AppCompatActivity {
    private Button greenButton, redButton, blueButton, yellowButton;
    private TextView tv_waiting;
    private Button b_play;
    private TextView tv_score, tv_beat;
    private TextView countdownTextView;
    private int score;
    private int highscore = 0;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers, mDatabaseNext, mDatabaseTurn, mDatabaseUser1, mDatabaseUser2;
    private ArrayList<Integer> sequence = new ArrayList<>();
    private static int userNumber = 0;
    private int turnSala;
    private String user1, user2;
    int nextSala;
    private int sequenceLength;
    private ArrayList<Integer> playerInput = new ArrayList<>();
    private Handler handler = new Handler();
    private boolean player_turn = false;
    private int currentIndex = 0;
    private int iniciado = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        // Initialize buttons and text views
        greenButton = findViewById(R.id.greenButton);
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        yellowButton = findViewById(R.id.yellowButton);
        tv_waiting = findViewById(R.id.tv_waiting);
        countdownTextView = findViewById(R.id.countdownTextView);
        tv_score = findViewById(R.id.tv_score);
        tv_beat = findViewById(R.id.tv_beat);
        Intent intent = getIntent();
        userNumber = intent.getIntExtra("userNumber", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Set initial values
        SharedPreferences preferences = getSharedPreferences("PREF", 0);
        highscore = preferences.getInt("highSimonscore", 0);
        tv_score.setText(getString(R.string.score_label, score));
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/users");
        mDatabaseNext = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas/Sala1/Next");
        mDatabaseTurn = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas/Sala1/Turn");
        mDatabaseUser1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas/Sala1/User1");
        mDatabaseUser2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ps-q2-10-default-rtdb.europe-west1.firebasedatabase.app/Salas/Sala1/User2");

        countdownTextView.setText("");

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

            if (userNumber==1){
                mDatabaseUser2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user2 = dataSnapshot.getValue(String.class);
                        Log.d("User2 Cambiado a ", String.valueOf(user2));

                        if (String.valueOf(user2).equals("")){
                            //countdownTextView.setText("YOU WON!");
                            if (iniciado==1){
                                Toast.makeText(SimonMultiplayer.this, "YOU WIN!! Congratilations", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            tv_waiting.setVisibility(View.INVISIBLE);

                        }else {
                            mDatabaseTurn.setValue(turnSala+1);
                            startGame();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        //finish();
                    }
                });

            }else if (userNumber==2){
                mDatabaseUser1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user1 = dataSnapshot.getValue(String.class);
                        if (Objects.equals(user1, "")){
                            if (iniciado==1){
                                Toast.makeText(SimonMultiplayer.this, "YOU WIN!! Congratilations", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }else {
                            //Iniciar juego
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        //finish();
                    }
                });
            }

            mDatabaseNext.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nextSala = dataSnapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //finish();
                }
            });

            mDatabaseTurn.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    turnSala = dataSnapshot.getValue(Integer.class);
                    if (turnSala==1)
                        iniciado=1;
                    if (turnSala!=0)
                        sequence.add(nextSala);
                    if (userNumber==1){
                        if (turnSala%2!=0){
                            startCountdown();
                            tv_waiting.setVisibility(View.INVISIBLE);
                        }else{
                            if (turnSala!=0)
                                countdownTextView.setText("Buena Eleccion!");
                            disableButtons();
                            tv_waiting.setVisibility(View.VISIBLE);
                        }
                    } else if (userNumber==2) {
                        if (turnSala%2==0 && turnSala!=0 ){
                            startCountdown();
                            tv_waiting.setVisibility(View.INVISIBLE);
                        }else{
                            if (turnSala<2)
                                countdownTextView.setText("");
                            else
                                countdownTextView.setText("Buena Eleccion!");
                            disableButtons();
                            tv_waiting.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    finish();
                }
            });


        }else{
            Log.d("Aqui?", "prueba");
            finish();
        }

        // Set button listeners
        greenButton.setOnClickListener(v -> onColorButtonClicked(0));
        redButton.setOnClickListener(v -> onColorButtonClicked(1));
        blueButton.setOnClickListener(v -> onColorButtonClicked(2));
        yellowButton.setOnClickListener(v -> onColorButtonClicked(3));
        disableButtons();
        if (userNumber==1){
            mDatabaseUser1.setValue(mAuth.getUid());
        } else if (userNumber==2){
            mDatabaseUser2.setValue(mAuth.getUid());
        }

    }

    private void startGame() {
        score = 0;
        sequenceLength = turnSala;
        playerInput.clear();
        sequence.clear();
        startCountdown();
        tv_waiting.setVisibility(View.INVISIBLE);
        iniciado=1;
    }

    private void restartGame() {
        Random random = new Random();
        mDatabaseNext.setValue(random.nextInt(4));
        mDatabaseTurn.setValue(0);
        disableButtons();
        tv_waiting.setVisibility(View.VISIBLE);
        player_turn = false;
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
        if (player_turn){
            if (playerInput.get(currentIndex) == sequence.get(currentIndex)) {
                currentIndex++;
                if (currentIndex == sequence.size()) {
                    currentIndex = 0;
                    playerInput.clear();
                    score++;
                    tv_score.setText(getString(R.string.score_label, score));
                    countdownTextView.setText("Select Next Color for your oponent!");
                    //generateSequence();
                    player_turn = false;

                    updateHighscore(score);
                }
            } else {
                Toast.makeText(this, "Error! YOU LOOSE", Toast.LENGTH_SHORT).show();
                mDatabaseNext.setValue(0);
                mDatabaseTurn.setValue(1);
                finish();
            }
        }else{
            mDatabaseNext.setValue(colorIndex);
            mDatabaseTurn.setValue(turnSala+1);
            playerInput.clear();
        }
    }

    private void updateHighscore(int score){

        if(mAuth.getUid() != null && isNetworkAvailable()) {


            mDatabaseUsers.orderByChild("uid").equalTo(mAuth.getUid())
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

                                        mDatabaseUsers.child(key).updateChildren(map);

                                        highscore = score;
                                        tv_beat.setText(getString(R.string.best, score));
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
                            Toast.makeText(SimonMultiplayer.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                            tv_beat.setText(getString(R.string.best, highscore));
                        }
                    });
        }else{
            if (score>highscore) {
                highscore = score;
                tv_beat.setText(getString(R.string.best, highscore));
                SharedPreferences preferences = getSharedPreferences("PREF", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("highSimonscore", highscore);
                editor.apply();
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (userNumber==1){
            Random random = new Random();
            mDatabaseNext.setValue(random.nextInt(4));
            mDatabaseTurn.setValue(0);
            mDatabaseUser1.setValue("");
            userNumber=0;
        }
        else if (userNumber==2) {
            Random random = new Random();
            mDatabaseNext.setValue(random.nextInt(4));
            mDatabaseUser2.setValue("");
            userNumber=0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (userNumber==1){
            Random random = new Random();
            mDatabaseNext.setValue(random.nextInt(4));
            mDatabaseTurn.setValue(0);
            mDatabaseUser1.setValue("");
            userNumber=0;
        }
        else if (userNumber==2) {
            Random random = new Random();
            mDatabaseNext.setValue(random.nextInt(4));
            mDatabaseUser2.setValue("");
            userNumber=0;
        }
    }
}
