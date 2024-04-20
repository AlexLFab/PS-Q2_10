package com.example.simonsays;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.circularreveal.CircularRevealHelper;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> sequence = new ArrayList<>();
    private ArrayList<Integer> playerInput = new ArrayList<>();
    private TextView scoreTextView;
    private Button greenButton, redButton, blueButton, yellowButton, bluetoothButton;
    private int score = 0;
    private int currentIndex = 0;

    private int sequenceLength = 5;
    private static final int REQUEST_ENABLE_BT = 1;


    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        greenButton = findViewById(R.id.greenButton);
        redButton = findViewById(R.id.redButton);
        blueButton = findViewById(R.id.blueButton);
        yellowButton = findViewById(R.id.yellowButton);
        bluetoothButton = findViewById(R.id.bluetoothButton);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter mBtAdapter = bluetoothManager.getAdapter();

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

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtAdapter == null) {
                    Toast.makeText(MainActivity.super.getApplicationContext(), "No soporta Bluetooth", Toast.LENGTH_SHORT).show();
                }
                if (!mBtAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                }
            }
        });

        final TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setText("3");

        startCountdown();
    }

    private void startCountdown() {
        final TextView countdownTextView = findViewById(R.id.countdownTextView);
        countdownTextView.setText("3");

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
                                countdownTextView.setText("¡Ya!");
                                // Inicia la secuencia después de mostrar "¡Ya!"
                                generateSequence();
                                showSequence();
                            }
                        }, 1000); // Espera 1 segundo antes de mostrar "¡Ya!"
                    }
                }, 1000); // Espera 1 segundo antes de mostrar "1"
            }
        }, 1000); // Espera 1 segundo antes de mostrar "2"
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
    private void showSequence() {
        disableColorButtons(); // Desactivar los botones durante la reproducción de la secuencia

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sequence.size(); i++) {
                    final int colorIndex = sequence.get(i);
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
                        }
                    }, i * 2000); // Resalta cada botón con un retraso de 2 segundos
                }

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableColorButtons(); // Activar los botones después de mostrar la secuencia
                    }
                }, sequence.size() * 2000 + 500); // Activar los botones después de que la secuencia haya terminado
            }
        }, 500);
    }


    private void highlightButton(int colorIndex) {
        int highlightColor;
        switch (colorIndex) {
            case 0:
                Log.d("Hola", "Estoy pasando por aqui");
                highlightColor = ContextCompat.getColor(this, R.color.white);
                greenButton.setBackgroundColor(highlightColor);
                break;
            case 1:
                highlightColor = ContextCompat.getColor(this,  R.color.white);
                redButton.setBackgroundColor(highlightColor);
                break;
            case 2:
                highlightColor = ContextCompat.getColor(this,  R.color.white);
                blueButton.setBackgroundColor(highlightColor);
                break;
            case 3:
                highlightColor = ContextCompat.getColor(this,  R.color.white);
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


    private void disableColorButtons() {
        greenButton.setEnabled(false);
        redButton.setEnabled(false);
        blueButton.setEnabled(false);
        yellowButton.setEnabled(false);
    }




    private void enableColorButtons() {
        greenButton.setEnabled(true);
        redButton.setEnabled(true);
        blueButton.setEnabled(true);
        yellowButton.setEnabled(true);
    }



    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}