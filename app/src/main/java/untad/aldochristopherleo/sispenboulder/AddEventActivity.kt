package untad.aldochristopherleo.sispenboulder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import untad.aldochristopherleo.sispenboulder.databinding.ActivityAddEventBinding
import android.text.format.DateFormat
import android.widget.Toast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Event
import java.text.SimpleDateFormat
import java.time.LocalDate
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

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Tambah Event")

        database = Firebase.database.reference

        bind.edtTime.editText?.setOnClickListener {
            openTimePicker()
        }

        bind.edtDate.editText?.setOnClickListener {
            openEditPicker()
        }

        bind.btnConfirmEventAdd.setOnClickListener {
            val name = bind.edtName.editText?.text.isNullOrEmpty()
            val date = bind.edtDate.editText?.text.isNullOrEmpty()
            val time = bind.edtTime.editText?.text.isNullOrEmpty()
            val location = bind.edtLocation.editText?.text.isNullOrEmpty()
            if (name || date || time || location){
                Toast.makeText(this, "Lengkapi Data Anda", Toast.LENGTH_SHORT).show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Apakah Anda Yakin Telah Benar?")
                    .setPositiveButton("Ya"){ _, _ ->
                        setEventDb()
                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Tidak"){ _, _ ->
                        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        }
    }

    private fun setEventDb() {
        val name = bind.edtName.editText?.text.toString().trim()
        val location = bind.edtLocation.editText?.text.toString().trim()
        val time = bind.edtTime.editText?.text.toString().trim()
        var dateStr = bind.edtDate.editText?.text.toString().trim()

        dateStr = "$dateStr $time"

        val l = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm"))
        Toast.makeText(this,dateStr,Toast.LENGTH_SHORT).show()
        val date = l.toInstant(ZoneId.systemDefault().rules.getOffset(l)).toEpochMilli()

        val event = Event(name, date, location, false, 0)

        database.child("events").child(name).setValue(event)
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
            if (minute < 10) displayMinute = "0$minute" else displayMinute = minute.toString()
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
}