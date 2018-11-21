package com.sergiocruz.Matematica.activity;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.google.ads.consent.ConsentStatus;
import com.sergiocruz.Matematica.R;
import com.sergiocruz.Matematica.helper.Ads;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);


        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        //preference.setOnPreferenceChangeListener(this);
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


/*    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        Log.i("Sergio>>>", "onPreferenceChange: value" + value);
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
        return true;
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.show_explanation)));

        Preference prefShowColors = findPreference(getString(R.string.show_colors));
        prefShowColors.setEnabled(false);

        Preference prefShowPerformance = findPreference(getString(R.string.show_performance));
        prefShowPerformance.setEnabled(false);

        final CheckBoxPreference bruteForce = (CheckBoxPreference) findPreference(getString(R.string.brute_force));
        final CheckBoxPreference probabilistic = (CheckBoxPreference) findPreference(getString(R.string.probabilistic_mode));

        bruteForce.setOnPreferenceClickListener(preference -> {
            probabilistic.setChecked(!bruteForce.isChecked());
            return true;
        });

        probabilistic.setOnPreferenceClickListener(preference -> {
            bruteForce.setChecked(!probabilistic.isChecked());
            return true;
        });


        Preference prefRevokeAds = findPreference(getString(R.string.revoke_ads));
        String title = getString(R.string.personalized_consent);

        String consentStatus;
        switch (Ads.getStatus(getApplicationContext())) {
            case PERSONALIZED:
                consentStatus = getString(R.string.enabled);
                break;
            case NON_PERSONALIZED:
                consentStatus = getString(R.string.disabled);
                break;
            default:
                consentStatus = getString(R.string.unknown);
                break;
        }

        prefRevokeAds.setSummary(title +  " " + consentStatus);

        prefRevokeAds.setOnPreferenceClickListener(preference -> {
            //Revoke Ads consent
            Ads.setStatus(getApplicationContext(), ConsentStatus.UNKNOWN);
            prefRevokeAds.setSummary(title + " " + getString(R.string.revoked));
            onBackPressed();
            return true;
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}