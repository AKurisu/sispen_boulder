package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class SettingFragment : PreferenceFragmentCompat(){

    private val viewModel: MainViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        if (viewModel.user.value?.type != "Admin"){
            val ahp1 = preferenceScreen.findPreference<Preference>("penilaian_kriteria")
            val ahp2 = preferenceScreen.findPreference<Preference>("hasil_penilaian_kriteria")

//            if (ahp1 != null && ahp2 != null) {
//                preferenceScreen.removePreference(ahp1)
//                preferenceScreen.removePreference(ahp2)
//            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        Toast.makeText(preference.context, preference.key.toString(), Toast.LENGTH_SHORT).show()
        if (preference.key.toString() == "logout"){
            Firebase.auth.signOut()
            AuthUI.getInstance().signOut(preference.context).addOnCompleteListener {
                activity?.finish()
            }.addOnFailureListener {
                Toast.makeText(preference.context, "Failed", Toast.LENGTH_SHORT).show()
            }
        } else if (preference.key.toString() == "delete_account"){
            val user = Firebase.auth.currentUser
            if (user != null) {
                FirebaseDatabase.getInstance().reference.child("users/${user.uid}").removeValue().addOnSuccessListener {
                    user.delete().addOnSuccessListener {
                        Toast.makeText(preference.context, "Akun Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                        activity?.finish()
                    }.addOnFailureListener {
                        Toast.makeText(preference.context, "Failed", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Toast.makeText(preference.context, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}