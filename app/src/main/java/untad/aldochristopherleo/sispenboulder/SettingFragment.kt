package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.databinding.ActivityMainBinding

class SettingFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        Toast.makeText(preference?.context, preference?.key.toString(), Toast.LENGTH_SHORT).show()
        if (preference?.key.toString() == "logout"){
            Firebase.auth.signOut()
            AuthUI.getInstance().signOut(preference?.context!!).addOnCompleteListener {
                activity?.finish()
            }.addOnFailureListener {
                Toast.makeText(preference.context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}