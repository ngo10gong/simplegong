package com.hk.simplenewsgong.simplegong;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

/**
 * A fragment handle setting of the app.  This fragment use preference to store the setting by
 * extending PreferenceFragmentCompat
 * For now, it only support language switching
 * <p>
 * Created by simplegong
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = SettingsFragment.class.getSimpleName();

    /**
     * set the summary string according to the selected preference
     *
     * @param preference preference that have just changed
     * @param value      selected value
     */
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            //for now, it only have one
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                //other than checkboxpreference, other preference have summary
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.gong_lang_pref))) {
            Log.d(TAG, " onSharedPreferenceChanged 1");
            Preference preference = findPreference(key);
            if (null != preference) {
                if (!(preference instanceof CheckBoxPreference)) {
                    //other than checkboxpreference, other preference have summary
                    setPreferenceSummary(preference, sharedPreferences.getString(key, activity.getString(R.string.choice_value_lang_eng)));
                }
            }
        } else {
            Log.d(TAG, " onSharedPreferenceChanged 2");
        }

    }
}

