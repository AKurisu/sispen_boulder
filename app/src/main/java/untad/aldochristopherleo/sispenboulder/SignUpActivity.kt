package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Participant
import untad.aldochristopherleo.sispenboulder.databinding.ActivitySignUpBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySignUpBinding
    private lateinit var database: DatabaseReference
    private lateinit var group: String
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Tambah Peserta"

        viewModel.user.observe(this){
            if (it.group != null){
                group = it.group
                bind.edtGroupname.editText?.setText(group)
            }
        }

        bind.edtName.editText?.doOnTextChanged { text, start, before, count ->
            if (bind.edtName.editText?.text.isNullOrBlank()){
                bind.edtName.error = "Text"
            } else {
                bind.edtName.error = null
            }
        }

        bind.btnConfirmationSignUp.setOnClickListener {
            val name = bind.edtName.editText?.text
            if (!name.isNullOrBlank()){
                MaterialAlertDialogBuilder(this)
                    .setTitle("Apakah Anda Yakin Telah Benar?")
                    .setMessage(name.toString())
                    .setPositiveButton("Ya"){ _, _ ->
                        setUserDb()
                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Tidak"){ _, _ ->
                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            } else {
                Toast.makeText(this, "Lengkapi Data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserDb() {

        database = Firebase.database.reference

        val groupName = bind.edtGroupname.editText?.text.toString()
        val name = bind.edtName.editText?.text.toString()
        val participant = Participant(name, groupName)
        val key = database.child("participant").push().key
        if (key == null){
            Log.w("TAG", "Couldn't get push key for posts")
            return
        }
        database.child("participant").child(key).setValue(participant)
    }
}