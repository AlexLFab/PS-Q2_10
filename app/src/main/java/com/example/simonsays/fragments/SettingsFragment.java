package com.example.simonsays.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.simonsays.R;
import com.example.simonsays.StartActivity;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Obtener la preferencia del modo nocturno
        SwitchPreferenceCompat nightModePreference = findPreference("night_mode");
        if (nightModePreference != null) {
            nightModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isNightMode = (boolean) newValue;
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
            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Reiniciar la actividad principal para aplicar el nuevo idioma
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
            });
        }
    }
}

