package untad.aldochristopherleo.sispenboulder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.Judge
import untad.aldochristopherleo.sispenboulder.data.User
import untad.aldochristopherleo.sispenboulder.databinding.ActivityAddJudgesBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddJudgesActivity : AppCompatActivity() {


    private var _bind : ActivityAddJudgesBinding? = null
    private val bind get() = _bind!!
    private lateinit var wallsList: ArrayList<TextInputLayout>
    private lateinit var database: DatabaseReference
    private lateinit var judges: ArrayList<String>
    private lateinit var alertBuilder : AlertDialog.Builder
    private var eventData: Event? = null
    private var eventKey : String? = null
    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityAddJudgesBinding.inflate(layoutInflater)
        setContentView(bind.root)

        eventData = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra("EXTRA_EVENT", Event::class.java)
        } else {
            intent.getParcelableExtra<Event>("EXTRA_EVENT") as Event
        }

        eventKey = intent.getStringExtra("EXTRA_EVENT_KEY")
        status = intent.getStringExtra("EXTRA_STATUS")

        wallsList = ArrayList()
        judges = ArrayList()

        val wall1 = bind.edtWall1
        val wall2 = bind.edtWall2
        val wall3 = bind.edtWall3
        val wall4 = bind.edtWall4
        val wall5 = bind.edtWall5

        Collections.addAll(wallsList, wall1, wall2, wall3, wall4, wall5)

        if (status == "EDIT"){
            var wall1 = ""
            var wall2 = ""
            var wall3 = ""
            var wall4 = ""
            var wall5 = ""
            if (!eventData?.judges.isNullOrEmpty()){
                eventData?.judges?.forEach{ (key, value) ->
                    if (key == "Dinding 1") wall1 = value.name.toString()
                    else if (key == "Dinding 2") wall2 = value.name.toString()
                    else if (key == "Dinding 3") wall3 = value.name.toString()
                    else if (key == "Dinding 4") wall4 = value.name.toString()
                    else if (key == "Dinding 5") wall5 = value.name.toString()
                }
            }
            var size = eventData?.judges?.size.toString()
            if (size == "") size = "0"
            bind.edtEntrynumber.editText?.setText(size)

            if (wall1.isNotEmpty()){
                bind.edtWall1.editText?.setText(wall1)
            }
            if (wall2.isNotEmpty()){
                bind.edtWall2.editText?.setText(wall2)
            }
            if (wall3.isNotEmpty()){
                bind.edtWall3.editText?.setText(wall3)
            }
            if (wall4.isNotEmpty()){
                bind.edtWall4.editText?.setText(wall4)
            }
            if (wall5.isNotEmpty()){
                bind.edtWall5.editText?.setText(wall5)
            }

            showTextBox()
        }

        alertBuilder = AlertDialog.Builder(this)

        database = Firebase.database.reference
        database.child("users").orderByChild("type").equalTo("Juri Lapangan").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
//                            val key = snapshot.key
                            val user = value.getValue(User::class.java)
                            judges.add(user?.name.toString())
                            Log.d("AddJudge", user.toString())
                        }
                        setList()
                    } else {
                        alertBuild("Error", "Tidak Ada Juri Lapangan Yang Terdaftar.")
                        alertBuilder.show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    alertBuild("Canceled", "Request Dibatalkan")
                    alertBuilder.show()
                }
            }
        )

        bind.btnWalls.setOnClickListener {
            showTextBox()

        }

        bind.btnConfirmJudges.setOnClickListener {
            confirmJudges()
        }
    }

    private fun alertBuild(title: String, message: String) {
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setNegativeButton("Tutup") {_, _ ->
            if (title == "Error"){
                finish()
            }
        }
    }

    private fun setList() {
        val adapter = ArrayAdapter(this,R.layout.list_type, judges)
        for (i in 1..wallsList.size){
            (wallsList[i-1].editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    private fun confirmJudges() {
        val wallsAdded = bind.edtEntrynumber.editText?.text.toString().toInt() - 1

        for (i in 0..wallsAdded){
            if (wallsList[i].editText?.text.isNullOrEmpty()) {
                Toast.makeText(this, "Pastikan semua dinding telah ada juri", Toast.LENGTH_SHORT).show()
                return
            }
//            for (ii in (i+1)..wallsAdded){
//                if (i == wallsAdded) break
//                if(wallsList[i].editText?.text.toString() == wallsList[ii].editText?.text.toString()){
//                    Toast.makeText(this, "Juri Lapangan Tidak Boleh Lebih Dari 1 Dinding", Toast.LENGTH_SHORT).show()
//                    return
//                }
//            }
        }
        saveData(wallsAdded)
    }

    private fun saveData(wallsAdded: Int) {
        val juri = HashMap<String, Judge>()
        for (i in 0..wallsAdded){
            val index = i+1
            juri["Dinding " + index] = Judge(wallsList[i].editText?.text.toString())
//            juri.add(Judge(wallsList[i].editText?.text.toString()))
        }

        if (eventData != null){
            database.child("events/${eventKey}/judges").setValue(juri)
            Toast.makeText(this, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        } else Toast.makeText(this, "Terjadi Masalah Koneksi", Toast.LENGTH_SHORT).show()


    }

    private fun showTextBox(){

        if (bind.edtEntrynumber.editText?.text.isNullOrBlank()) {
            bind.edtEntrynumber.error = "Input Tidak Boleh Kosong"
            return
        }

        val wallsToAdd = bind.edtEntrynumber.editText?.text.toString().toInt()

        if (wallsToAdd > 5){
            hideWalls()
            bind.edtEntrynumber.hint = "Tidak Lebih Dari 5"
            return
        } else if (wallsToAdd < 1){
            hideWalls()
        }

        for (i in 1..wallsToAdd){
            if (i == 1){
                hideWalls()
            }
            wallsList[i-1].visibility = View.VISIBLE
        }

        bind.btnConfirmJudges.visibility = View.VISIBLE
    }

    private fun hideWalls() {
        for (i in 1..5){
            wallsList[i-1].visibility = View.GONE
        }
    }
}