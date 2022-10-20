package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.adapter.ListParticipantAdapter
import untad.aldochristopherleo.sispenboulder.data.*
import untad.aldochristopherleo.sispenboulder.databinding.ActivityEventBinding
import untad.aldochristopherleo.sispenboulder.util.Ahp
import untad.aldochristopherleo.sispenboulder.util.MainViewModel
import untad.aldochristopherleo.sispenboulder.util.Topsis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

@Suppress("DEPRECATION")
class EventActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    private var userType : String? = null
    private var userName : String? = null
    private var _bind : ActivityEventBinding? = null
    private val database = Firebase.database.reference
    private val bind get() = _bind!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var menu: Menu
    private lateinit var result : LinkedHashMap<String, Result>
    private lateinit var statusEvent : String
    private lateinit var booleanArray: BooleanArray
    private lateinit var stringArray: Array<String>
    private lateinit var resultArray: ArrayList<Double>
    private lateinit var sortedResult: ArrayList<SortedResult>
    private lateinit var sortedResultSend: ArrayList<SortedResult>
    private lateinit var adapter : ListParticipantAdapter
    private lateinit var event : Event
    private lateinit var topsis : Topsis
    private lateinit var allParticipant : ArrayList<Participant>
    private lateinit var allParticipantKey : ArrayList<String>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var refreshListener: SwipeRefreshLayout.OnRefreshListener
    private lateinit var judgeKey: String
    private val participantList = ArrayList<Alternative>()
    private val allParticipantList = ArrayList<String>()
    private val checkedItems = ArrayList<Boolean>()
    private val choosenParticipant = ArrayList<Participant>()
    private val choosenKey = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _bind = ActivityEventBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Detail Event"

        bind.layoutHide.visibility = View.GONE
        bind.progressBar.visibility = View.VISIBLE
        swipeRefreshLayout = bind.eventRefresh

        event = intent.getParcelableExtra<Event>(EXTRA_EVENT) as Event
        allParticipant = ArrayList()
        allParticipantKey = ArrayList()

        result = LinkedHashMap()
        sortedResult = ArrayList()
        sortedResultSend = ArrayList()
        resultArray = ArrayList()

        statusEvent = if (event.status != null) event.status!! else "KOSONG"

        database.child("events/${event.name}/status").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                statusEvent = snapshot.value.toString()
                checkEventStatus()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        rv = bind.participantRv
        adapter = ListParticipantAdapter(sortedResult)
        checkEventParticipant()

        refreshListener = SwipeRefreshLayout.OnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            checkEventParticipant()
            swipeRefreshLayout.isRefreshing = false
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewModel.getAllParticipants().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val group = item.getValue(Participant::class.java)
                    val nama = item.child("name").value.toString()
                    var compareList = false
                    Log.d("ADDPART", group?.name.toString())
                    allParticipantKey.add(item.key.toString())
                    allParticipant.add(group!!)
                    allParticipantList.add(nama+" ("+ group.group +")")
                    for (itemlist in participantList){
                        if (itemlist.name == nama){
                            choosenParticipant.add(group)
                            choosenKey.add(item.key.toString())
                            Log.d("Event", itemlist.name)
                            compareList = true
                        }
                    }
                    checkedItems.add(compareList)
                }
                checkEventStatus()
                populateSpinner(allParticipantList, checkedItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }

        })

        viewModel.user.observe(this) {
            if (it.type == "Manajer") bind.btnEventEdit.visibility = View.GONE
            bind.btnEventEdit.text = when (it.type) {
                "Juri Lapangan" -> "Nilai Peserta"
                "Presiden Juri" -> "Pilih Juri Lapangan"
                else -> " "
            }
            userType = it.type
            userName = it.name
            checkEventStatus()
        }



        if (event.finished){
            bind.btnEventEdit.visibility = View.GONE
        }

        (event.name + " (" + event.location + ")").also { bind.eventName.text = it }

        setViewText()

        val inputEditText = EditText(this)
        inputEditText.setText(event.name)

        bind.eventDateEdit.setOnClickListener {
            openDatePicker()
        }

        bind.eventTimeEdit.setOnClickListener {
            openTimePicker()
        }

        bind.btnEventEdit.setOnClickListener{
            when (userType) {
                "Panitia" -> {
                    if (bind.btnEventEdit.text.toString() == "Mulai Perlombaan"){
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Apakah Lomba Siap Dimulai")
                            .setMessage("Jumlah Peserta" + participantList.size.toString())
                            .setPositiveButton("Ya"){_,_ ->
                                database.child("events/${event.name}/status")
                                    .setValue("LOMBA")
                                    .addOnSuccessListener {
                                        checkEventStatus()
                                    }
                            }
                            .show()
                    } else {
                        setButtonAction(choosenParticipant, choosenKey)
                    }
                }
                "Juri Lapangan" -> {
                    val intent = Intent(this, GradingActivity::class.java)
                    intent.putExtra(GradingActivity.EXTRA_EVENT, event)
                    intent.putExtra(GradingActivity.EXTRA_KEY, judgeKey)
                    startActivity(intent)
                }
                "Presiden Juri" -> {
                    val intent = Intent(this, AddJudgesActivity::class.java)
                    intent.putExtra("EXTRA_EVENT", event)
                    startActivity(intent)
                }
            }
        }

        swipeRefreshLayout.setOnRefreshListener(refreshListener)

    }

    private fun setViewText() {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            getResources().getConfiguration().locale;
        }
        val date = Date(event.date)
        val dateFormat = SimpleDateFormat("E, dd-MM-yyyy", locale)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        val dateText = dateFormat.format(date)
        val timeText = timeFormat.format(date)

        bind.eventDate.text = dateText
        bind.eventTime.text = timeText
        bind.eventTotalParticipant.text = getString(R.string.txt_jumlah_peserta, event.totalParticipant.toString())
    }

    private fun checkEventParticipant() {
        Log.d("CheckFirst", sortedResult.size.toString())

        viewModel.getParticipant(event.name).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sortedResult.clear()
                    sortedResultSend.clear()
                    participantList.clear()
                    result.clear()
                    for (item in snapshot.children) {
                        val participant = item.getValue(Participant::class.java)
                        val participantKey = item.key
                        participantList.add(Alternative(participant?.name))

                        viewModel.getResult(event.name).get().addOnSuccessListener {
                            var data = Result()
                            if(it.hasChild(participantKey!!)){
                                if (participant != null) {
                                    Log.d("CHECKPART",it.toString())
                                    result[participant.name + " ("+ participant.group + ")"] =
                                        it.child("$participantKey/Total").getValue(Result::class.java)!!
                                    data = it.child("$participantKey/Total").getValue(Result::class.java)!!
                                }
                            } else if (participant != null) {
                                result[participant.name + " ("+ participant.group + ")"] = Result()
                            }

                            resultArray.addAll(data.toArrayList())
                            Log.d("OnDataChange",(snapshot.childrenCount * 4).toString() +" "+ resultArray.size.toLong().toString())

                            if ((snapshot.childrenCount * 4) == resultArray.size.toLong()){
                                setPosition(snapshot.childrenCount)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())
                }
            })
    }

    private fun checkEventStatus(){
        if (statusEvent == "KOSONG") {
            MaterialAlertDialogBuilder(this)
                .setTitle("Event Bermasalah")
                .setMessage("Mohon Hubungi Panitia Event")
                .setPositiveButton("OK"){_,_ ->
                    finish()
                }
        } else if (statusEvent == "PERSIAPAN") {
            if (userType == "Juri Lapangan") {
                bind.btnEventEdit.visibility = View.GONE
            } else if (userType == "Panitia") {
                if (!event.judges.isNullOrEmpty()){
                    if (event.judges!!.size == 4 && participantList.size >= 8){
                        bind.btnEventEdit.text = "Mulai Perlombaan"
                        Toast.makeText(this, "Mulai Perlombaan", Toast.LENGTH_SHORT).show()
//                        checkEventStatus()
                    } else bind.btnEventEdit.text = "Tambah Peserta"
                } else bind.btnEventEdit.text = "Tambah Peserta"
            }
        } else if (statusEvent == "LOMBA"){
            run stop@{
                event.judges?.forEach { (key, value) ->
                    if (value.name == userName && userType == "Juri Lapangan"){
                        judgeKey = key
                        bind.btnEventEdit.visibility = View.VISIBLE
                        return@stop
                    } else bind.btnEventEdit.visibility = View.GONE
                }
            }
        }
    }

    private fun setButtonAction(
        participant: ArrayList<Participant>,
        key: ArrayList<String>
    ) {
        var choosenParticipant = participant
        var choosenKey = key
            val saveChoosenParticipant = choosenParticipant
            val saveChoosenKey = choosenKey
            MaterialAlertDialogBuilder(this)
                .setTitle("Tambah Peserta")
                .setNeutralButton("Cancel") { _, _ ->
                    choosenParticipant = saveChoosenParticipant
                    choosenKey = saveChoosenKey
                }
                .setPositiveButton("Ya") { _, _ ->
                    viewModel.getParticipant(event.name).removeValue()
                    if (choosenParticipant.isNotEmpty()){
                        val listChoosenParticipant = HashMap<String, Participant>()
                        for (index in choosenParticipant.indices){
                            listChoosenParticipant[choosenKey[index]] = choosenParticipant[index]

//                            database.child("events/${event.name}/participant/${choosenKey[index]}")
//                                .setValue(choosenParticipant[index])
                            //Simpan Data Participant
                        }
                        database.child("events/${event.name}/participant")
                            .setValue(listChoosenParticipant)
                    }
                    database.child("events/${event.name}/totalParticipant")
                        .setValue(choosenParticipant.size)

                    checkEventParticipant()
//                    swipeRefreshLayout.setOnRefreshListener(refreshListener)
                }
                .setMultiChoiceItems(stringArray, booleanArray) { _, which, checked ->
                    if (checked){
                        choosenParticipant.add(allParticipant[which])
                        choosenKey.add(allParticipantKey[which])
                    } else {
                        choosenParticipant.remove(allParticipant[which])
                        choosenKey.remove(allParticipantKey[which])
                    }
                }
                .show()
    }

    private fun setPosition(size: Long) {
        if ((size * 4) == resultArray.size.toLong()){
            val priority = viewModel.ahpPriority.value
            if (priority!!.isNotEmpty()){
                val ahp = Ahp(priority)
                topsis = Topsis(ahp.getConsistencySum(), resultArray)
                val resultTopsis = topsis.getPreference()

                for ((index, item) in result.keys.withIndex()){
                    Log.d("CHECK", result.size.toString())
                    sortedResult.add(SortedResult(item, resultTopsis[index], result[item]))
                }

                sortedResult.sortByDescending { it.preferenceValue }
                sortedResultSend= sortedResult
                resultArray.clear()

//                adapter.clearData()
                Log.d("ADAPTEREvent", sortedResult.size.toString())
                adapter.addAll(sortedResult)

                swipeRefreshLayout.isRefreshing = false
            }
        }
        Toast.makeText(this, "View Tampil", Toast.LENGTH_SHORT).show()
        showView()
    }

    private fun showView() {
        showMenuOptions()
        bind.layoutHide.visibility = View.VISIBLE
        bind.layoutHide.animate().alpha(1.0f)
        bind.progressBar.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_ahp -> {
                val intent = Intent(this, CriteriaResultActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_topsis -> {
                if (topsis.getNormalization().isNotEmpty()){
                    val intent = Intent(this, TopsisActivity::class.java)
                    intent.putExtra(TopsisActivity.EXTRA_EVENT, sortedResultSend)
                    intent.putExtra(TopsisActivity.EXTRA_TOPSIS, DataTopsis(topsis.getNormalization(),
                        topsis.getPriorityNormalize(), topsis.getdPosNeg("+"), topsis.getdPosNeg("-")))
                    startActivity(intent)
                }
                true
            }
            R.id.menu_hapus_event -> {
                MaterialAlertDialogBuilder(this)
                    .setMessage("Apakah Anda Yakin Ingin Menghapus Ini?")
                    .setPositiveButton("Ya"){_,_ ->
                        database.child("events/${event.name}").removeValue().addOnSuccessListener {
                            Toast.makeText(this, "Event berhasil dihapus", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    .setNegativeButton("Tidak"){_,_ ->
                    }

                true
            }
            R.id.menu_edit_event -> {
                if (event.status != "SELESAI"){
                    if(bind.eventDateEdit.visibility == View.GONE){
                        bind.eventDateEdit.visibility = View.VISIBLE
                        bind.eventTimeEdit.visibility = View.VISIBLE
                    } else {
                        bind.eventDateEdit.visibility = View.GONE
                        bind.eventTimeEdit.visibility = View.GONE
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateSpinner(allParticipant: ArrayList<String>, checkedItems: ArrayList<Boolean>) {
        stringArray = allParticipant.toArray(arrayOfNulls<String>(allParticipant.size))

        booleanArray = BooleanArray(checkedItems.size)
        for (index in checkedItems.indices){
            booleanArray[index] = checkedItems[index]
        }
//
//        bind.layoutHide.visibility = View.VISIBLE
//        bind.layoutHide.animate().alpha(1.0f)
//        bind.progressBar.visibility = View.GONE
    }

    private fun openDatePicker() {
        val date = event.date

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
        }
    }

    private fun openTimePicker() {
        val date = event.date
        val hour = Date(date).hours
        val minute = Date(date).minutes
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
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        val menuAhp = menu?.findItem(R.id.menu_ahp)
//        val menuTopsis = menu?.findItem(R.id.menu_topsis)
//
//        val menuUbah = menu?.findItem(R.id.menu_edit_event)
//        val menuHapus = menu?.findItem(R.id.menu_hapus_event)
//
//        if (userType != "Admin") {
//            menuAhp?.isVisible = false
//            menuTopsis?.isVisible = false
//        }
//        if (userType != "Panitia"){
//            menuUbah?.isVisible = false
//            menuHapus?.isVisible = false
//        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showMenuOptions() {
        val menuAhp = menu.findItem(R.id.menu_ahp)
        val menuTopsis = menu.findItem(R.id.menu_topsis)

        val menuUbah = menu.findItem(R.id.menu_edit_event)
        val menuHapus = menu.findItem(R.id.menu_hapus_event)

        if (userType != "Admin") {
            menuAhp?.isVisible = false
            menuTopsis?.isVisible = false
        }
        if (userType != "Panitia"){
            menuUbah?.isVisible = false
            menuHapus?.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var bool = false
        this.menu = menu
        if(event.totalParticipant > 0){
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.event_menu, menu)
            bool = true
        }
        return bool
    }
}