package com.example.manisharana.sunshine;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final EditTextPreference cityPref = (EditTextPreference) getPreferenceManager().findPreference(getActivity().getString(R.string.preference_default_city));
        final ListPreference unitPref = (ListPreference) getPreferenceManager().findPreference(getActivity().getString(R.string.preference_default_unit));
        final EditTextPreference daysPref = (EditTextPreference) getPreferenceManager().findPreference(getActivity().getString(R.string.preference_default_days));

        setSummary(cityPref,unitPref,daysPref);
        cityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                cityPref.setSummary((String)o);
                return true;
            }
        });

        unitPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                unitPref.setSummary((String)o);
                return true;
            }
        });

        daysPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                daysPref.setSummary((String)o);
                return true;
            }
        });
        return super.onCreateView(inflater,container,savedInstanceState);
    }

    private void setSummary(EditTextPreference cityPref, ListPreference unitPref, EditTextPreference daysPref) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCity = sharedPreferences.getString(getString(R.string.preference_default_city), getString(R.string.Bangalore));
        String defUnit = sharedPreferences.getString(getString(R.string.preference_default_unit), getString(R.string.metric));
        String defDays = sharedPreferences.getString(getString(R.string.preference_default_days), getString(R.string.week));

        cityPref.setSummary(defCity);
        unitPref.setSummary(defUnit);
        daysPref.setSummary(defDays);
    }
}
