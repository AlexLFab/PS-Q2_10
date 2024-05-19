package com.example.simonsays.Simon;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.simonsays.R;

import java.util.ArrayList;
import java.util.Random;

public class SimonActivity extends AppCompatActivity {

    private ArrayList<Integer> sequence = new ArrayList<>();
    private ArrayList<Integer> playerInput = new ArrayList<>();
    private TextView countdownTextView;
    private Button greenButton, redButton, blueButton, yellowButton;
    private Button b_play;
    private TextView tv_score, tv_beat;
    private int currentIndex = 0;
    private int sequenceLength = 5;

    private int score = 0;
    private int highscore = 0;

    private boolean player_turn = false;

    private Handler handler = new Handler();

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

        // Set initial values
        SharedPreferences preferences = getSharedPreferences("PREF", 0);
        highscore = preferences.getInt("highSimonscore", 0);
        tv_score.setText(getString(R.string.score_label, score));
        tv_beat.setText(getString(R.string.best, highscore));

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
        sequenceLength = 5;
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
        sequence.clear(); // Clear the old sequence
        Random random = new Random();
        for (int i = 0; i < sequenceLength; i++) {
            int next = random.nextInt(4);
            sequence.add(next); // 0: green, 1: red, 2: blue, 3: yellow
            Log.d("Color Sequence: ", String.valueOf(next));
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
                tv_score.setText(getString(R.string.score_label, score));
                generateSequence();
                player_turn = false;
                startCountdown();
                if (score % 5 == 0) {
                    sequenceLength++;
                }
                if (score > highscore) {
                    highscore = score;
                    tv_beat.setText(getString(R.string.best, highscore));
                    SharedPreferences preferences = getSharedPreferences("PREF", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("highSimonscore", highscore);
                    editor.apply();
                }
            }
        } else {
            Toast.makeText(this, "Error! Try again.", Toast.LENGTH_SHORT).show();
            currentIndex = 0;
            playerInput.clear();
            score = 0;
            tv_score.setText(getString(R.string.score_label, score));
            generateSequence();
            player_turn = false;
            startCountdown();
            if (sequenceLength > 5) {
                sequenceLength--;
            }
        }
    }
}