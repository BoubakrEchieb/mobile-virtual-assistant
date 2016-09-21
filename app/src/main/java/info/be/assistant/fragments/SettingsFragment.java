package info.be.assistant.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import info.be.assistant.R;

/**
 * Created by be on 17/09/16.
 */
public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
