package com.example.simonsays.Music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.example.simonsays.R;

public class AudioService extends Service {
    private MediaPlayer loop;
    public static boolean isMusicPlaying = false;

    public static final int DECREASE = 1, INCREASE = 2, START = 3, PAUSE = 4, SET_VOLUME = 5;

    @Override
    public void onCreate() {
        super.onCreate();
        loop = MediaPlayer.create(this, R.raw.amazing_future);
        loop.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.hasExtra("action")) {
            int action = intent.getIntExtra("action", -1);
            switch (action) {
                case INCREASE:
                    increaseVolume();
                    break;
                case DECREASE:
                    decreaseVolume();
                    break;
                case START:
                    startMusic();
                    break;
                case PAUSE:
                    pauseMusic();
                    break;
                case SET_VOLUME:
                    float volume = intent.getFloatExtra("volume", 1.0f);
                    setVolume(volume);
                    break;
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startMusic() {
        if (!loop.isPlaying()) {
            loop.start();
            isMusicPlaying = true;
        }
    }

    private void pauseMusic() {
        if (loop.isPlaying()) {
            loop.pause();
            isMusicPlaying = false;
        }
    }

    private void increaseVolume() {
        if (isMusicPlaying) {
            loop.setVolume(1.0f, 1.0f);
        }
    }

    private void decreaseVolume() {
        if (isMusicPlaying) {
            loop.setVolume(0.2f, 0.2f);
        }
    }

    private void setVolume(float volume) {
        if (isMusicPlaying) {
            loop.setVolume(volume, volume);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loop != null) {
            loop.stop();
            loop.release();
            loop = null;
        }
    }
}