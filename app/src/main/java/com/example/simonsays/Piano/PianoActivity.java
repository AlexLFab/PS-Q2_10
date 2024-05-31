package com.example.simonsays.Piano;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simonsays.Music.AudioService;
import com.example.simonsays.R;
import com.example.simonsays.databinding.ActivityPianoBinding;

import java.util.Random;

public class PianoActivity extends AppCompatActivity {
    ActivityPianoBinding binding;


    Random r;

    int rockLocationRow1, rockLocationRow2, rockLocationRow3, rockLocationRow4, rockLocationRow5;

    int frameImage, pawInFrameImage, tapImage, emptyImage;

    int currentScore = 0;
    int beatScore=0;

    CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPianoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        SharedPreferences preferences = getSharedPreferences("PREF", 0);
        beatScore = preferences.getInt("highscore", 0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        binding.include1.tvScore.setText(getString(R.string.score) + currentScore);

        binding.include1.tvBeat.setText(getString(R.string.best)+ beatScore);
        binding.include1.tvTime.setText(getString(R.string.time) + millisToTime(15000) );

        r=new Random();

        loadImages();

        timer = new CountDownTimer(15000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.include1.tvTime.setText(getString(R.string.time) + millisToTime(millisUntilFinished) );
            }

            @Override
            public void onFinish() {
                binding.include1.tvTime.setText(getString(R.string.time) + millisToTime(0) );

                binding.include2.iv31.setEnabled(false);
                binding.include2.iv32.setEnabled(false);
                binding.include2.iv33.setEnabled(false);
                binding.include3.bPlay.setVisibility(View.VISIBLE);

                binding.include2.iv11.setImageResource(emptyImage);
                binding.include2.iv12.setImageResource(emptyImage);
                binding.include2.iv13.setImageResource(emptyImage);

                binding.include2.iv21.setImageResource(emptyImage);
                binding.include2.iv22.setImageResource(emptyImage);
                binding.include2.iv23.setImageResource(emptyImage);

                binding.include2.iv31.setImageResource(emptyImage);
                binding.include2.iv32.setImageResource(emptyImage);
                binding.include2.iv33.setImageResource(emptyImage);

                binding.include2.iv41.setImageResource(emptyImage);
                binding.include2.iv42.setImageResource(emptyImage);
                binding.include2.iv43.setImageResource(emptyImage);

                binding.include2.iv51.setImageResource(emptyImage);
                binding.include2.iv52.setImageResource(emptyImage);
                binding.include2.iv53.setImageResource(emptyImage);

                if (currentScore>beatScore){
                    beatScore = currentScore;
                    binding.include1.tvBeat.setText(getString(R.string.best)+ beatScore);

                    SharedPreferences preferences1 = getSharedPreferences("PREF", 0);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putInt("highscore", beatScore);
                    editor.apply();
                }
            }
        };
        binding.include2.iv31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rockLocationRow3 == 1){
                    continueGame();
                }else{
                    endGame();
                }
            }
        });


        binding.include2.iv32.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(rockLocationRow3 == 2){
                continueGame();
            }else{
                endGame();
            }
        }
    });

        binding.include2.iv33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rockLocationRow3 == 3){
                    continueGame();
                }else{
                    endGame();
                }
            }
        });
        binding.include3.bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGame();
            }
        });
}

    private void continueGame(){
        rockLocationRow5 = rockLocationRow4;
        setRockLocation(rockLocationRow5, 5);

        rockLocationRow4 = rockLocationRow3;
        setRockLocation(rockLocationRow4, 4);

        rockLocationRow3 = rockLocationRow2;
        setRockLocation(rockLocationRow3, 3);

        rockLocationRow2 = rockLocationRow1;
        setRockLocation(rockLocationRow2, 2);

        rockLocationRow1 = r.nextInt(3) +1;
        setRockLocation(rockLocationRow1, 1);

        currentScore++;
        binding.include1.tvScore.setText(getString(R.string.score) + currentScore);
    }

    private void initGame(){
        binding.include2.iv31.setEnabled(true);
        binding.include2.iv32.setEnabled(true);
        binding.include2.iv33.setEnabled(true);
        binding.include3.bPlay.setVisibility(View.INVISIBLE);

        currentScore = 0;
        binding.include1.tvScore.setText(getString(R.string.score) + currentScore);
        timer.start();

        rockLocationRow4 =2;
        binding.include2.iv42.setImageResource(pawInFrameImage);

        rockLocationRow3 =2;
        binding.include2.iv32.setImageResource(tapImage);

        rockLocationRow2 =r.nextInt(3)+1;
        setRockLocation(rockLocationRow2, 2);

        rockLocationRow1 =r.nextInt(3)+1;
        setRockLocation(rockLocationRow1, 1);
    }

    private void endGame(){
        timer.cancel();

        binding.include2.iv31.setEnabled(false);
        binding.include2.iv32.setEnabled(false);
        binding.include2.iv33.setEnabled(false);
        binding.include3.bPlay.setVisibility(View.VISIBLE);

        binding.include2.iv11.setImageResource(emptyImage);
        binding.include2.iv12.setImageResource(emptyImage);
        binding.include2.iv13.setImageResource(emptyImage);

        binding.include2.iv21.setImageResource(emptyImage);
        binding.include2.iv22.setImageResource(emptyImage);
        binding.include2.iv23.setImageResource(emptyImage);

        binding.include2.iv31.setImageResource(emptyImage);
        binding.include2.iv32.setImageResource(emptyImage);
        binding.include2.iv33.setImageResource(emptyImage);

        binding.include2.iv41.setImageResource(emptyImage);
        binding.include2.iv42.setImageResource(emptyImage);
        binding.include2.iv43.setImageResource(emptyImage);

        binding.include2.iv51.setImageResource(emptyImage);
        binding.include2.iv52.setImageResource(emptyImage);
        binding.include2.iv53.setImageResource(emptyImage);

        Toast.makeText(PianoActivity.this, "Failed", Toast.LENGTH_SHORT).show();

    }

    private void setRockLocation(int place, int row){
        if (row ==1){
            binding.include2.iv11.setImageResource(emptyImage);
            binding.include2.iv12.setImageResource(emptyImage);
            binding.include2.iv13.setImageResource(emptyImage);

            switch (place){
                case 1:
                    binding.include2.iv11.setImageResource(frameImage);
                    break;
                case 2:
                    binding.include2.iv12.setImageResource(frameImage);
                    break;
                case 3:
                    binding.include2.iv13.setImageResource(frameImage);
                    break;
            }
        }
        if (row ==2){
            binding.include2.iv21.setImageResource(emptyImage);
            binding.include2.iv22.setImageResource(emptyImage);
            binding.include2.iv23.setImageResource(emptyImage);

            switch (place){
                case 1:
                    binding.include2.iv21.setImageResource(frameImage);
                    break;
                case 2:
                    binding.include2.iv22.setImageResource(frameImage);
                    break;
                case 3:
                    binding.include2.iv23.setImageResource(frameImage);
                    break;
            }
        }

        if (row ==3){
            binding.include2.iv31.setImageResource(emptyImage);
            binding.include2.iv32.setImageResource(emptyImage);
            binding.include2.iv33.setImageResource(emptyImage);

            switch (place){
                case 1:
                    binding.include2.iv31.setImageResource(tapImage);
                    break;
                case 2:
                    binding.include2.iv32.setImageResource(tapImage);
                    break;
                case 3:
                    binding.include2.iv33.setImageResource(tapImage);
                    break;
            }
        }

        if (row ==4){
            binding.include2.iv41.setImageResource(emptyImage);
            binding.include2.iv42.setImageResource(emptyImage);
            binding.include2.iv43.setImageResource(emptyImage);

            switch (place){
                case 1:
                    binding.include2.iv41.setImageResource(pawInFrameImage);
                    break;
                case 2:
                    binding.include2.iv42.setImageResource(pawInFrameImage);
                    break;
                case 3:
                    binding.include2.iv43.setImageResource(pawInFrameImage);
                    break;
            }
        }

        if (row ==5){
            binding.include2.iv51.setImageResource(emptyImage);
            binding.include2.iv52.setImageResource(emptyImage);
            binding.include2.iv53.setImageResource(emptyImage);

            switch (place){
                case 1:
                    binding.include2.iv51.setImageResource(frameImage);
                    break;
                case 2:
                    binding.include2.iv52.setImageResource(frameImage);
                    break;
                case 3:
                    binding.include2.iv53.setImageResource(frameImage);
                    break;
            }
        }

    }

    private int millisToTime(long millis) {
        return (int) millis / 1000;
    }

    private void loadImages(){
        frameImage = R.drawable.ic_frame;
        pawInFrameImage = R.drawable.ic_paw_frame;
        tapImage = R.drawable.ic_tap;

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