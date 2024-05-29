package com.example.simonsays.Music;

import android.app.Application;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.simonsays.R;

public class MusicViewModel extends AndroidViewModel {

    private MediaPlayer mediaPlayer;

    public MusicViewModel(@NonNull Application application) {
        super(application);
        mediaPlayer = MediaPlayer.create(application, R.raw.amazing_future); // Asegúrate de que tienes el archivo mp3 en res/raw
        mediaPlayer.setLooping(true); // Repetir la música
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}