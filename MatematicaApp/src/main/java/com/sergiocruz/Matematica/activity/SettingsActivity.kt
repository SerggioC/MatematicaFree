package com.sergiocruz.Matematica.activity


import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import com.google.ads.consent.ConsentStatus
import com.sergiocruz.Matematica.R
import com.sergiocruz.Matematica.helper.Ads

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see .sBindPreferenceSummaryToValueListener
     */
    private fun bindPreferenceSummaryToValue(preference: Preference) {
        // Set the listener to watch for value changes.
        //preference.setOnPreferenceChangeListener(this);
        val sOnPreferenceChangeListener = Preference.OnPreferenceChangeListener(fun(preference: Preference?, newValue: Any?): Boolean {
            val stringValue = newValue.toString()
            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val listPreference = preference
                val index = listPreference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(if (index >= 0) listPreference.entries[index] else null)

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference?.summary = stringValue
            }
            return true
        })
        preference.onPreferenceChangeListener = sOnPreferenceChangeListener

        // Trigger the listener immediately with the preference's
        // current value.
        sOnPreferenceChangeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getString(preference.key, ""))
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        addPreferencesFromResource(R.xml.pref_general)
        bindPreferenceSummaryToValue(findPreference(getString(R.string.show_explanation)))

        val prefShowColors = findPreference(getString(R.string.show_colors))
        prefShowColors.isEnabled = false

        val prefShowPerformance = findPreference(getString(R.string.show_performance))
        prefShowPerformance.isEnabled = false

        val bruteForce = findPreference(getString(R.string.brute_force)) as CheckBoxPreference
        val probabilistic = findPreference(getString(R.string.probabilistic_mode)) as CheckBoxPreference

        bruteForce.setOnPreferenceClickListener {
            probabilistic.isChecked = !bruteForce.isChecked
            true
        }

        probabilistic.setOnPreferenceClickListener {
            bruteForce.isChecked = !probabilistic.isChecked
            true
        }


        val prefRevokeAds = findPreference(getString(R.string.revoke_ads))
        val title = getString(R.string.personalized_consent)

        val consentStatus: String = when (Ads.getStatus(applicationContext)) {
            ConsentStatus.PERSONALIZED -> getString(R.string.enabled)
            ConsentStatus.NON_PERSONALIZED -> getString(R.string.disabled)
            else -> getString(R.string.unknown)
        }

        prefRevokeAds.summary = "$title $consentStatus"

        prefRevokeAds.setOnPreferenceClickListener {
            //Revoke Ads consent
            Ads.setStatus(applicationContext, ConsentStatus.UNKNOWN)
            prefRevokeAds.summary = title + " " + getString(R.string.revoked)
            onBackPressed()
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


}