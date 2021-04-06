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

    public static final String DEFAULT_SERVER_URL = "http://192.168.100.189:5000/get-prediction";
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

        /*
         * This registerOnSharedPreferenceChangeListener is called after any shared preference element is updated.
         *
         * Use of this function: This function is used as the default value setter for serverUrl Filed.
         */
        serverURL.getSharedPreferences().registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(SERVER_URL_KEY)) {
                    if (sharedPreferences.getString(key, DEFAULT_SERVER_URL).equals("")) {
                        serverURL.setText(DEFAULT_SERVER_URL);
                    }
                }
            }
        });

        /*
        * This setOnPreferenceChangeListener is called before updating the shared preference field so any changed made in setOnPreferenceChangeListener
            to the same element in shared preference will be replaced with setOnPreferenceChangeListener data.
        * Since, the change made to element from this function is overwritten by this function it is suitable to use this function as validator function.
        *
        * Use of this Function:  If the data is valid then only onSharedPreferenceChanged is called.
        */
        serverURL.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return newValue.equals("") || URLUtil.isHttpUrl(newValue.toString()) || URLUtil.isHttpsUrl(newValue.toString());
            }
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