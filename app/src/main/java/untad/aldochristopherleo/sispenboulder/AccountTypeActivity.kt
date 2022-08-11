package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.User
import untad.aldochristopherleo.sispenboulder.databinding.ActivityAccountTypeBinding

class AccountTypeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAccountTypeBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAccountTypeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.textInputLayout2.visibility = View.GONE

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        database = Firebase.database.reference

        val items = listOf("Panitia", "Presiden Juri", "Juri Lapangan", "Manajer")
        val adapter = ArrayAdapter(this, R.layout.list_type, items)
        (bind.dropdownType as? AutoCompleteTextView)?.setAdapter(adapter)

        bind.dropdownType.setOnItemClickListener { adapterView, view, i, l ->
            if (bind.dropdownType.text.toString() == "Manajer"){
                bind.textInputLayout2.visibility = View.VISIBLE
            } else bind.textInputLayout2.visibility = View.GONE
        }

        val inputText = bind.dropdownType.text.isNullOrEmpty()
        bind.btnTypeConfirm.setOnClickListener {
            if (inputText){
                val uid = user?.uid
                val currentUser = User(user?.displayName, user?.email, bind.dropdownType.text.toString())
                val currentUserValue = currentUser.toMap()

                val update = hashMapOf<String, Any>(
                    "/users/$uid" to currentUserValue
                )

                database.updateChildren(update).addOnSuccessListener {
                    val dashboardIntent = Intent(this, MainActivity::class.java)
                    startActivity(dashboardIntent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, inputText.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}