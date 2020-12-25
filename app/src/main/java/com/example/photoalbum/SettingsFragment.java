package com.example.photoalbum;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference privacyPolicy;
    private EditTextPreference serverURL;

    public static final String DEFAULT_SERVER_URL = "https://127.0.0.1";
    public static final boolean DEFAULT_GENERATE_DESCRIPTION = true;
    public static final boolean DEFAULT_READ_DESCRIPTION = true;
    public static final String SERVER_URL_KEY = "serverUrl";
    public static final String READ_DESCRIPTION_KEY = "readDescription";
    public static final String GENERATE_DESCRIPTION_KEY = "description";
    public static final String PRIVACY_POLICY_KEY = "privacyPolicy";


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        privacyPolicy = findPreference(PRIVACY_POLICY_KEY);
        serverURL = findPreference(SERVER_URL_KEY);

        // shared preference onchange is called ehn shared preference is updated
        // is used as the default checker
        serverURL.getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(SERVER_URL_KEY)){
                    if(sharedPreferences.getString(key,DEFAULT_SERVER_URL).equals("")) {
                        serverURL.setText(DEFAULT_SERVER_URL);
                    }
                }
            }
        });

        // on preference change is called when before updating the shared preference so any changed made from this
        // to the same element in shared preference will be replaced. this is used to validate the data.
        // if the daa is valid then only shared preference change is called
        serverURL.setOnPreferenceChangeListener((preference, newValue) -> {
            if(!newValue.equals("")){
                return URLUtil.isHttpsUrl(newValue.toString());
            }
            return true;
        });

        try {
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