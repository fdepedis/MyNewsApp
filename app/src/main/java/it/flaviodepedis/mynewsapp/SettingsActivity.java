package it.flaviodepedis.mynewsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsItemPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Given the key of a count event meteo preferences, we can use PreferenceFragment's findPreference()
            // method to get the Preference object and setup the preference
            //Preference countEvent = findPreference(getString(R.string.settings_count_key));
            //bindPreferenceSummaryToValue(countEvent);

            //Preference languages = findPreference(getString(R.string.settings_languages_key));
            //bindPreferenceSummaryToValue(languages);
        }


        // E' un metodo con una struttura abbastanza standard e riutilizzabile in altri progetti
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();
            // Se il parametro "preference" è una lista trova l'indice del valore
            // e salvalo nelle preference, altrimenti gestisci dei campi di testo (EditText)
            // per inserimento di valori
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        // E' un metodo con una struttura abbastanza standard e riutilizzabile in altri progetti
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

    }
}
