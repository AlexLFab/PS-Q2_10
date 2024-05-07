package com.example.simonsays;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> sequence = new ArrayList<>();
    private ArrayList<Integer> playerInput = new ArrayList<>();
    private TextView scoreTextView;
    private Button greenButton, redButton, blueButton, yellowButton;
    private int score = 0;
    private int currentIndex = 0;
    private int sequenceLength = 5;

    private boolean player_turn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreTextView = findViewById(R.id.scoreTextView);
        greenButton = findViewById(R.id.greenButton);
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        yellowButton = findViewById(R.id.yellowButton);

        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorButtonClicked(0);
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorButtonClicked(1);
            }
        });

        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorButtonClicked(2);
            }
        });

        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorButtonClicked(3);
            }
        });
        disableButtons();
        final TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setText("3");

        startCountdown();
    }

    private void startCountdown() {
        final TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setText("3");
        if (!player_turn) {
            // Inicia una cuenta regresiva antes de comenzar la secuencia
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countdownTextView.setText("2");
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countdownTextView.setText("1");
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    countdownTextView.setText("Pay Attention!");
                                    // Inicia la secuencia después de mostrar "¡Ya!"
                                    generateSequence();
                                    showSequence();
                                }
                            }, 1000); // Espera 1 segundo antes de mostrar "¡Ya!"
                        }
                    }, 1000); // Espera 1 segundo antes de mostrar "1"
                }
            }, 1000); // Espera 1 segundo antes de mostrar "2"
        }else{
            // Inicia una cuenta regresiva antes de comenzar la secuencia
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countdownTextView.setText("2");
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countdownTextView.setText("1");
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    countdownTextView.setText("Your Turn!");
                                    // Inicia la secuencia después de mostrar "¡Ya!"
                                }
                            }, 1000); // Espera 1 segundo antes de mostrar "¡Ya!"
                        }
                    }, 1000); // Espera 1 segundo antes de mostrar "1"
                }
            }, 1000); // Espera 1 segundo antes de mostrar "2"

        }
    }

    private void generateSequence() {
        Random random = new Random();
        sequence.clear();
        for (int i = 0; i < sequenceLength; i++) { // Generar una secuencia de longitud variable
            int next = random.nextInt(4);
            sequence.add(next); // 0: verde, 1: rojo, 2: azul, 3: amarillo
            Log.d("Secuencia de colores: ", String.valueOf(next));
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
        player_turn=false;
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
                                player_turn=true;
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
                Log.d("Hola", "Estoy pasando por aqui");
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
        int normalColor;
        normalColor = ContextCompat.getColor(this, R.color.normal_green);
        greenButton.setBackgroundColor(normalColor);
        normalColor = ContextCompat.getColor(this, R.color.normal_red);
        redButton.setBackgroundColor(normalColor);
        normalColor = ContextCompat.getColor(this, R.color.normal_blue);
        blueButton.setBackgroundColor(normalColor);
        normalColor = ContextCompat.getColor(this, R.color.normal_yellow);
        yellowButton.setBackgroundColor(normalColor);
    }

    private void onColorButtonClicked(int colorIndex) {
            playerInput.add(colorIndex);
            if (playerInput.get(currentIndex) == sequence.get(currentIndex)) {
                currentIndex++;
                if (currentIndex == sequence.size()) {
                    currentIndex = 0;
                    playerInput.clear();
                    score++;
                    scoreTextView.setText(getString(R.string.score_label, score));
                    generateSequence();
                    startCountdown();
                    if (score % 5 == 0) { // Aumentar la longitud de la secuencia cada 5 aciertos
                        sequenceLength++;
                    }
                }
            } else {
                Toast.makeText(this, "¡Error! Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                currentIndex = 0;
                playerInput.clear();
                score = 0;
                scoreTextView.setText(getString(R.string.score_label, score));
                generateSequence();
                startCountdown();
                if (sequenceLength > 5) { // Reducir la longitud de la secuencia si ya es mayor que 5
                    sequenceLength--;
                }
            }

    }

}