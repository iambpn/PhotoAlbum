package com.example.photoalbum;

import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String DEFAULT_SERVER_URL = "http://192.168.100.189:5000/get-prediction";
    public static final boolean DEFAULT_GENERATE_DESCRIPTION = true;
    public static final boolean DEFAULT_READ_DESCRIPTION = true;
    public static final String SERVER_URL_KEY = "serverUrl";
    public static final String READ_DESCRIPTION_KEY = "readDescription";
    public static final String GENERATE_DESCRIPTION_KEY = "description";
    public static final String PRIVACY_POLICY_KEY = "privacyPolicy";
    private Preference privacyPolicy;
    private EditTextPreference serverURL;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        privacyPolicy = findPreference(PRIVACY_POLICY_KEY);
        serverURL = findPreference(SERVER_URL_KEY);

        /*
        * This registerOnSharedPreferenceChangeListener is called after any shared preference element is updated.
        * Use of this function: This function is used as the default value setter for serverUrl Filed.
        * eg: serverURL.getSharedPreferences().registerOnSharedPreferenceChangeListener()
        *
        *
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
                    // show privacy policy dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Privacy Policy");
                    dialog.setMessage("We use your personal data for training our AI model and improve the captions. Currently this policy is not implemented but soon we will change to this. Users will get the opt out option when this policy is implemented.");
                    dialog.setCancelable(true);
                    dialog.setNegativeButton("Ok", null);
                    dialog.show();
                    return true;
                });
            } else {
                throw new Exception("Settings not found");
            }
        } catch (Exception ex) {
            Log.e("Settings", "onCreatePreferences: " + ex.getMessage(), ex);
            Toast.makeText(getContext(), "Error:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.setting_header);
    }
}