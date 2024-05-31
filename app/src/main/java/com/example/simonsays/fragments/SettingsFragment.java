package com.example.simonsays.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.example.simonsays.Music.AudioService;
import com.example.simonsays.R;
import com.example.simonsays.StartActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

    private boolean wasMusicPlaying = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Obtener la preferencia del modo nocturno
        SwitchPreferenceCompat nightModePreference = findPreference("night_mode");
        if (nightModePreference != null) {
            nightModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isNightMode = (boolean) newValue;
                wasMusicPlaying = isMusicPlaying();
                if (isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            });
        }

        // Obtener la preferencia de idioma
        ListPreference languagePreference = findPreference("language_preference");
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                // Reiniciar la actividad principal para aplicar el nuevo idioma
                wasMusicPlaying = isMusicPlaying();
                Intent intent = new Intent(getActivity(), StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                return true;
            });
        }

        // Obtener la preferencia del volumen de la música
        SeekBarPreference volumePreference = findPreference("music_volume");
        if (volumePreference != null) {
            volumePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                int newVolume = (int) newValue;
                // Inicializar o actualizar el servicio de música con el nuevo volumen
                initializeAudioService(newVolume);
                return true;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wasMusicPlaying) {
            Intent intent = new Intent(requireContext(), AudioService.class);
            intent.putExtra("action", AudioService.START);
            requireActivity().startService(intent);
        }
    }

    // Método para inicializar o actualizar el servicio de música con el volumen proporcionado
    private void initializeAudioService(int volume) {
        Intent musicIntent = new Intent(requireContext(), AudioService.class);
        musicIntent.putExtra("action", AudioService.SET_VOLUME);
        musicIntent.putExtra("volume", volume / 100.0f); // Convertir a escala de 0 a 1
        requireActivity().startService(musicIntent);
    }

    private boolean isMusicPlaying() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return prefs.getBoolean("is_music_playing", false);
    }
}