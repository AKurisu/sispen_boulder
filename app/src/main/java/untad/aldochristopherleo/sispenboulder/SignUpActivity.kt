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

    companion object{
        const val EXTRA_PARTICIPANT_KEY = "extra_participant_key"
        const val EXTRA_PARTICIPANT_NAME = "extra_participant_name"
        const val EXTRA_PARTICIPANT_GROUP = "extra_participant_group"
    }

    private lateinit var bind: ActivitySignUpBinding
    private lateinit var database: DatabaseReference
    private lateinit var key : String
    private lateinit var updateName : String
    private lateinit var updateGroup : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Tambah Peserta"

        if (intent.hasExtra("extra_participant_key") ){
            key = intent.getStringExtra(EXTRA_PARTICIPANT_KEY).toString()
            updateName = intent.getStringExtra(EXTRA_PARTICIPANT_NAME).toString()
            updateGroup = intent.getStringExtra(EXTRA_PARTICIPANT_GROUP).toString()
            bind.edtName.editText?.setText(updateName)
            bind.edtGroupname.editText?.setText(updateGroup)
        } else {
            key = ""
            updateName = ""
            updateGroup = ""
        }

//        viewModel.user.observe(this){
//            if (it.group != null){
//                group = it.group
//                bind.edtGroupname.editText?.setText(group)
//            }
//        }

        bind.edtName.editText?.doOnTextChanged { _, _, _, _ ->
            if (bind.edtName.editText?.text.isNullOrBlank()){
                bind.edtName.error = "Nama Harus Diisi"
            } else {
                bind.edtName.error = null
            }
        }

        bind.btnConfirmationSignUp.setOnClickListener {
            val name = bind.edtName.editText?.text
            val groupName = bind.edtGroupname.editText?.text
            if (!name.isNullOrBlank() || !groupName.isNullOrBlank()){
                if (name.toString() == updateName && groupName.toString() == updateGroup){
                    Toast.makeText(this, "Data Tidak Memiliki Perubahan", Toast.LENGTH_SHORT).show()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Apakah Anda Yakin Data Peserta Telah Benar?")
                        .setMessage(name.toString())
                        .setPositiveButton("Ya"){ _, _ ->
                            setUserDb()
                        }
                        .setNegativeButton("Tidak"){ _, _ ->
                            Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                }
            } else {
                bind.edtName.error = "Nama Harus Diisi"
                Toast.makeText(this, "Lengkapi Data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserDb() {
        database = Firebase.database.reference

        val groupName = bind.edtGroupname.editText?.text.toString()
        val name = bind.edtName.editText?.text.toString()
        val participant = Participant(name, groupName)

        if (key == ""){
            database.child("participant").orderByChild("name")
                .equalTo(name).get().addOnSuccessListener {
                    if (it.exists()){
                        var count = 0
                        for (item in it.children){
                            val checkGroup = item.child("group").value.toString()
                            if (checkGroup == groupName){
                                Toast.makeText(this, "Terdapat Data Yang Sama Di Database", Toast.LENGTH_SHORT).show()
                            } else if (count == it.children.count()) {
                                addParticipant(participant)
                            }
                            count++
                        }
                    } else {
                        addParticipant(participant)
                    }
                }
        } else {
            database.child("participant").child(key).setValue(participant)
        }
    }

    private fun addParticipant(participant : Participant){
        val key = database.child("participant").push().key
        if (key == null){
            Log.w("TAG", "Couldn't get push key for posts")
        } else database.child("participant").child(key).setValue(participant)
        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
        finish()
    }
}