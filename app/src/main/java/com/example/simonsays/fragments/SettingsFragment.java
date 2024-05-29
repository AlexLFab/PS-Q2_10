package com.example.simonsays.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.simonsays.R;

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
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String language = (String) newValue;
                setLocale(language);
                return true;
            });
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Recargar la actividad para aplicar el nuevo idioma
        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
}
