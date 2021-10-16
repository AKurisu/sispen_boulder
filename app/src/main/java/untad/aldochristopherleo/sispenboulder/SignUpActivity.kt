package untad.aldochristopherleo.sispenboulder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Alternative
import untad.aldochristopherleo.sispenboulder.databinding.ActivitySignUpBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class SignUpActivity : AppCompatActivity() {

    private lateinit var bind: ActivitySignUpBinding
    private lateinit var database: DatabaseReference
    private var totalData = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Tambah Peserta")

        database = Firebase.database.reference
        database.child("participant").child("total").get().addOnSuccessListener {
            totalData = it.value.toString().toInt()
            val input = totalData + 1
            bind.edtEntrynumber.editText?.setText(input.toString())
            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            bind.edtEntrynumber.editText?.setText("1")
            Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
        }

        bind.edtName.editText?.doOnTextChanged { text, start, before, count ->
            if (bind.edtName.editText?.text.isNullOrBlank()){
                bind.edtName.error = "Text"
            } else {
                bind.edtName.error = null
            }
        }
        bind.edtTelephone.editText?.doOnTextChanged { text, start, before, count ->
            if (bind.edtTelephone.editText?.text.isNullOrBlank()){
                bind.edtTelephone.error = "Text"
            } else {
                bind.edtTelephone.error = null
            }
        }

        bind.btnConfirmationSignUp.setOnClickListener {
            val telephone = bind.edtTelephone.editText?.text
            val name = bind.edtName.editText?.text
            if (!telephone.isNullOrBlank() &&
                !name.isNullOrBlank()){
                MaterialAlertDialogBuilder(this)
                    .setTitle("Apakah Anda Yakin Telah Benar?")
                    .setMessage(name.toString() + ' ' + telephone.toString())
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

        val entry = bind.edtEntrynumber.editText?.text.toString().toInt()
        val name = bind.edtName.editText?.text.toString()
        val phone = bind.edtTelephone.editText?.text.toString().trim().toInt()
        val mUid = FirebaseAuth.getInstance().currentUser!!.uid
        val alternative = Alternative(name, phone)
        val key = database.child("participant").push().key
        if (key == null){
            Log.w("TAG", "Couldn't get push key for posts")
            return
        }
        database.child("participant").child(mUid).child(key).setValue(alternative)
        database.child("participant").child("total").setValue(entry)
    }
}