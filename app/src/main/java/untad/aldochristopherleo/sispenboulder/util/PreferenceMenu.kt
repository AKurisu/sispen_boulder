package untad.aldochristopherleo.sispenboulder.util

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import untad.aldochristopherleo.sispenboulder.R

class PreferenceMenu: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }
}