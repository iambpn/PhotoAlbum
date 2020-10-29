package com.example.photoalbum;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference privacyPolicy;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        try {
            privacyPolicy = findPreference("privacyPolicy");
            if (privacyPolicy != null) {
                privacyPolicy.setOnPreferenceClickListener(preference -> {
                    Toast.makeText(getContext(), "Privacy policy is not yet available.", Toast.LENGTH_SHORT).show();
                    return true;
                });
            } else {
                throw new Exception("Settings not found");
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Error:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.setting_header);
    }
}