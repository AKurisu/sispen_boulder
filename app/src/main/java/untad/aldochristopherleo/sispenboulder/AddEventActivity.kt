package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.databinding.ActivityAddEventBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private var _bind : ActivityAddEventBinding? = null
    private val bind get() = _bind!!
    private var hour = 0
    private var displayHour = ""
    private var minute = 0
    private var displayMinute = ""
    private var date : Long = 0
    private lateinit var alertBuilder : AlertDialog.Builder
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        title = "Tambah Event"

        alertBuilder = AlertDialog.Builder(this)
        alertBuild()

        val judgesList = ArrayList<String>()
        val adapter = ArrayAdapter(this, R.layout.list_type, judgesList)

        database = Firebase.database.reference
        database.child("users").orderByChild("type").equalTo("Presiden Juri").
        addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (i in snapshot.children){
                        judgesList.add(i.child("name").value.toString())
                    }
                    (bind.edtPresidentList as? AutoCompleteTextView)?.setAdapter(adapter)
                }
                else {
                    alertBuilder.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        bind.edtTime.editText?.setOnClickListener {
            openTimePicker()
        }

        bind.edtDate.editText?.setOnClickListener {
            openEditPicker()
        }

        bind.btnConfirmEventAdd.setOnClickListener {
            val name = bind.edtName.editText?.text
            val date = bind.edtDate.editText?.text
            val time = bind.edtTime.editText?.text
            val location = bind.edtLocation.editText?.text
            val president = bind.edtPresident.editText?.text
            val coordinator = bind.edtCoordinator.editText?.text
            val reviewer = bind.edtReviewer.editText?.text
            if (name.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Nama")
            else if (date.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Tanggal")
            else if (time.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Waktu")
            else if (location.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Lokasi")
            else if (president.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Presiden")
            else if (coordinator.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Panitia Pelaksana")
            else if (reviewer.isNullOrEmpty()) bind.edtName.editText?.error = getString(R.string.txt_error, "Utusan")
            else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Apakah Anda Yakin Telah Benar?")
                    .setMessage(
                                "Nama: ${name}\n" +
                                "Tanggal: ${date}\n" +
                                "Waktu: ${time}\n" +
                                "Lokasi: ${location}\n"+
                                "Presiden Juri: ${president}\n"+
                                "Panpel: ${coordinator}\n"+
                                "Utusan: ${reviewer}\n")
                    .setPositiveButton("Ya"){ _, _ ->
                        setEventDb()
                    }
                    .setNegativeButton("Tidak"){ _, _ ->
//                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }

    private fun alertBuild() {
        alertBuilder.setTitle("Juri Belum Ada")
        alertBuilder.setMessage("Belum ada Presiden Juri yang terdaftar pada aplikasi!")
        alertBuilder.setNegativeButton("Tutup") {_, _ ->
            finish()
        }
    }

    private fun setEventDb() {
        val name = bind.edtName.editText?.text.toString().trim()
        val location = bind.edtLocation.editText?.text.toString().trim()
        val time = bind.edtTime.editText?.text.toString().trim()
        var dateStr = bind.edtDate.editText?.text.toString().trim()
        val president = bind.edtPresident.editText?.text.toString().trim()
        val coordinator = bind.edtCoordinator.editText?.text.toString().trim()
        val reviewer = bind.edtReviewer.editText?.text.toString().trim()

        dateStr = "$dateStr $time"

        val l = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm"))
        Toast.makeText(this,dateStr,Toast.LENGTH_SHORT).show()
        val date = l.toInstant(ZoneId.systemDefault().rules.getOffset(l)).toEpochMilli()
        val key = database.child("events").push().key.toString()

        val event = Event(name, date, location, false, 0, president, status = "PERSIAPAN", null, null, coordinator, reviewer)

        database.child("events").child(key).setValue(event).addOnSuccessListener {
            Toast.makeText(this, "Lomba Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Lomba Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEditPicker() {
        if (date.compareTo(0) == 0){
            date = MaterialDatePicker.todayInUtcMilliseconds()
        }

        val constraintBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val picker = MaterialDatePicker.Builder
            .datePicker()
            .setSelection(date)
            .setCalendarConstraints(constraintBuilder)
            .setTitleText("Tanggal Event")
            .build()

        picker.show(supportFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            date = it
            bind.edtDate.editText?.setText(getDateTime())
        }
    }

    private fun openTimePicker() {
        if (bind.edtTime.editText?.text.isNullOrEmpty()){
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            minute = Calendar.getInstance().get(Calendar.MINUTE)
        }
        val isSystem24Hour = DateFormat.is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText(R.string.waktu_event)
            .build()

        picker.show(supportFragmentManager, "TAG")

        picker.addOnPositiveButtonClickListener {
            hour = picker.hour
            minute = picker.minute
            if (hour < 10) displayHour = "0$hour" else displayHour = hour.toString()
            displayMinute = if (minute < 10) "0$minute" else minute.toString()
            bind.edtTime.editText?.setText(getString(R.string.display_time, displayHour, displayMinute))
        }
    }

    private fun getDateTime(): String? {
        try {
            val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
            val netDate = Date(date)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (bind.edtName.editText!!.text.isNotEmpty()
            || bind.edtDate.editText!!.text.isNotEmpty()
            || bind.edtTime.editText!!.text.isNotEmpty()
            || bind.edtLocation.editText!!.text.isNotEmpty()
            || bind.edtPresident.editText!!.text.isNotEmpty()) {
            MaterialAlertDialogBuilder(this,
            com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setMessage("Apakah Anda Yakin Ingin Kembali?")
                .setNegativeButton("Ya") {_,_ ->
                    super.onBackPressed()
//                    finish()
                }
                .setPositiveButton("Tidak"){_,_ ->}
                .show()
        } else super.onBackPressed()
    }
}