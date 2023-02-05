package untad.aldochristopherleo.sispenboulder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.adapter.ListParticipantAdapter
import untad.aldochristopherleo.sispenboulder.data.*
import untad.aldochristopherleo.sispenboulder.databinding.ActivityEventBinding
import untad.aldochristopherleo.sispenboulder.util.Ahp
import untad.aldochristopherleo.sispenboulder.util.MainViewModel
import untad.aldochristopherleo.sispenboulder.util.PrintData
import untad.aldochristopherleo.sispenboulder.util.Topsis
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


@Suppress("DEPRECATION")
class EventActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
        const val EXTRA_EVENT_KEY = "extra_event_key"
        const val PERMISSION_REQUEST_CODE = 0
    }

    private var firstOpen = 0
    private lateinit var notCountKeys: ArrayList<String>
    private lateinit var oldList: ArrayList<SortedResult>
    private var userType : String? = null
    private var userName : String? = null
    private var _bind : ActivityEventBinding? = null
    private val database = Firebase.database.reference
    private val bind get() = _bind!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var eventKey: String
    private lateinit var menu: Menu
    private lateinit var result : LinkedHashMap<String, Result>
    private lateinit var resultParticipantKey : ArrayList<String>
    private lateinit var resultWall1 : ArrayList<Result>
    private lateinit var resultWall2 : ArrayList<Result>
    private lateinit var resultWall3 : ArrayList<Result>
    private lateinit var resultWall4 : ArrayList<Result>
    private lateinit var resultWall5 : ArrayList<Result>
    private lateinit var judgesList : ArrayList<String>
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
    private var refreshPulledTime : Long = 0
    private val participantList = ArrayList<Alternative>()
    private val allParticipantList = ArrayList<String>()
    private val checkedItems = ArrayList<Boolean>()
    private val gradedOrderList = ArrayList<Int>()
    private val choosenParticipant = ArrayList<Participant>()
    private val choosenKey = ArrayList<String>()
    private val wallHasBeenCount = LinkedHashMap<String, ArrayList<Int>>()
    private var participantSize = 0
    private var participantHasBeenCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _bind = ActivityEventBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Detail Event"

        bind.layoutHide.visibility = View.GONE
        bind.progressBar.visibility = View.VISIBLE
        swipeRefreshLayout = bind.eventRefresh

        eventKey = intent.getStringExtra(GradingActivity.EXTRA_EVENT_KEY).toString()
        event = intent.getParcelableExtra<Event>(EXTRA_EVENT) as Event
        allParticipant = ArrayList()
        allParticipantKey = ArrayList()

        result = LinkedHashMap()
        sortedResult = ArrayList()
        sortedResultSend = ArrayList()
        resultArray = ArrayList()
        resultWall1 = ArrayList()
        resultWall2 = ArrayList()
        resultWall3 = ArrayList()
        resultWall4 = ArrayList()
        resultWall5 = ArrayList()
        resultParticipantKey = ArrayList()
        judgesList = ArrayList()
        oldList = ArrayList()
        notCountKeys = ArrayList()

        event.judges?.forEach { (_, item) ->
            item.name?.let { judgesList.add(it) }
        }

        statusEvent = if (event.status != null) event.status!! else "KOSONG"


        database.child("events/${eventKey}/status").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                statusEvent = snapshot.value.toString()
                checkEventStatus()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        rv = bind.participantRv
        rv.layoutManager = LinearLayoutManager(this)
        adapter = ListParticipantAdapter(sortedResult, eventKey, event)
        checkEventParticipant()

        refreshListener = SwipeRefreshLayout.OnRefreshListener {
            if (refreshPulledTime + 60000 > System.currentTimeMillis()){
                Toast.makeText(this, "Tunggu 1 menit sebelum refresh ulang", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            } else {
                checkEventStatus()
                swipeRefreshLayout.isRefreshing = true
                getEventData()
                checkEventParticipant()
                swipeRefreshLayout.isRefreshing = false
            }

            refreshPulledTime = System.currentTimeMillis()
        }

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

        rv.adapter = adapter

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

            showMenuOptions()
        }

        if (event.finished){
            bind.btnEventEdit.visibility = View.GONE
        }

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
                    setParticipantEvent(choosenParticipant, choosenKey)
                }
                "Juri Lapangan" -> {
                    val intent = Intent(this, GradingActivity::class.java)
                    intent.putExtra(GradingActivity.EXTRA_EVENT_KEY, eventKey)
                    intent.putExtra(GradingActivity.EXTRA_EVENT, event)
                    intent.putExtra(GradingActivity.EXTRA_NAME, userName)
                    intent.putExtra(GradingActivity.EXTRA_ACTIVITY_FROM, "EVENT")
                    activityResult.launch(intent)
                }
                "Presiden Juri" -> {
                    setParticipantEvent(choosenParticipant, choosenKey)
                }
            }
        }

        swipeRefreshLayout.setOnRefreshListener(refreshListener)

    }

    private fun getEventData(){
        database.child("events/$eventKey").addValueEventListener(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latestEventData : Event? = snapshot.getValue(Event::class.java)
                    if (latestEventData != null){
                        if (event != latestEventData){
                            event = latestEventData
                            setViewText()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("EVENT_DATA", error.toString())
                }

            }
        )
    }

    private fun setViewText() {
        val locale =
            resources.configuration.locales.get(0)
        val date = Date(event.date)
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        val timeFormat = SimpleDateFormat("HH:mm z", locale)
        val dateText = dateFormat.format(date)
        val timeText = timeFormat.format(date)

        (event.name + " (" + event.location + ")").also { bind.eventName.text = it }
        bind.eventDate.text = dateText
        bind.eventTime.text = timeText
        bind.eventTotalParticipant.text = getString(R.string.txt_jumlah_peserta, event.totalParticipant.toString())
    }

    private fun checkEventParticipant() {
        Log.d("CheckFirst", sortedResult.size.toString())

        viewModel.getParticipant(eventKey).get().addOnSuccessListener { snapshot ->
            Log.d("CheckVM", snapshot.toString())
            oldList = sortedResult
            clearList()

            if (snapshot.value != null){
                for (item in snapshot.children) {
                    val participant = item.getValue(Participant::class.java)
                    val participantKey = item.key
                    participantList.add(Alternative(participant?.name))
                    participantSize++

                    viewModel.getResult(eventKey).get().addOnSuccessListener {
                        var data = Result()
                        if(it.hasChild(participantKey!!)){
                            if (participant != null) {
                                Log.d("CHECKPART",it.toString())
                                var countedWall = 0
                                val listOfWall = ArrayList<Int>()


                                val order = if (it.child("$participantKey/order").exists()){
                                    (it.child("$participantKey/order").value as Long).toInt()
                                } else -1

                                gradedOrderList.add(order)
                                Log.d("CHECKEVENT", order.toString())

                                result[participant.name + " ("+ participant.group + ")"] =
                                    it.child("$participantKey/Total").getValue(Result::class.java)!!
                                data = it.child("$participantKey/Total").getValue(Result::class.java)!!
                                resultParticipantKey.add(participantKey)

                                if ( it.child("$participantKey/Dinding 1").exists() ){
                                    countedWall++
                                    listOfWall.add(1)
                                    resultWall1.add(it.child("$participantKey/Dinding 1").getValue(Result::class.java)!!)
                                } else resultWall1.add(Result())
                                if ( it.child("$participantKey/Dinding 2").exists() ){
                                    countedWall++
                                    listOfWall.add(2)
                                    resultWall2.add(it.child("$participantKey/Dinding 2").getValue(Result::class.java)!!)
                                } else resultWall2.add(Result())
                                if ( it.child("$participantKey/Dinding 3").exists() ){
                                    countedWall++
                                    listOfWall.add(3)
                                    resultWall3.add(it.child("$participantKey/Dinding 3").getValue(Result::class.java)!!)
                                } else resultWall3.add(Result())
                                if ( it.child("$participantKey/Dinding 4").exists() ){
                                    countedWall++
                                    listOfWall.add(4)
                                    resultWall4.add(it.child("$participantKey/Dinding 4").getValue(Result::class.java)!!)
                                } else resultWall4.add(Result())
                                if ( it.child("$participantKey/Dinding 5").exists() ){
                                    countedWall++
                                    listOfWall.add(5)
                                    resultWall5.add(it.child("$participantKey/Dinding 5").getValue(Result::class.java)!!)
                                } else resultWall5.add(Result())

                                wallHasBeenCount[participantKey] = listOfWall
                                if (event.judges?.size == 5){
                                    if (countedWall == 5){
                                        participantHasBeenCount++
                                    }
                                } else {
                                    if (countedWall >= 4){
                                        participantHasBeenCount++
                                    }
                                }
                            }
                        } else if (participant != null) {
                            result[participant.name + " ("+ participant.group + ")"] = Result()
                            gradedOrderList.add(-1)
                            resultParticipantKey.add(participantKey)
                            resultWall1.add(Result())
                            resultWall2.add(Result())
                            resultWall3.add(Result())
                            resultWall4.add(Result())
                            resultWall5.add(Result())
                        }

                        Log.d("OnDataChange",resultWall1.size.toString())


                        resultArray.addAll(data.toArrayList())
                        Log.d("OnDataChange",(snapshot.childrenCount * 4).toString() +" "+ resultArray.size.toLong().toString())

                        if ((snapshot.childrenCount * 4) == resultArray.size.toLong()){
                            setPosition(snapshot.childrenCount)
                        }
                    }
                }
            } else showView()
        }
    }

    private fun clearList() {
        sortedResult.clear()
        sortedResultSend.clear()
        participantList.clear()
        result.clear()
        resultWall1.clear()
        resultWall2.clear()
        resultWall3.clear()
        resultWall4.clear()
        resultWall5.clear()
        resultParticipantKey.clear()
        notCountKeys.clear()
    }

    private fun checkEventStatus(){
        Log.d("CheckEvent", "tes")
        if (statusEvent == "KOSONG") {
            MaterialAlertDialogBuilder(this)
                .setTitle("Event Bermasalah")
                .setMessage("Mohon Hubungi Panitia Event")
                .setPositiveButton("OK"){_,_ ->
                    finish()
                }
        } else if (statusEvent == "PERSIAPAN") {
            bind.txtEventStatus.text = "Event Belum Dimulai"
            bind.eventName.setTextColor(Color.YELLOW)
            if (!event.judges.isNullOrEmpty()){
                if (event.judges!!.size >= 4 && participantList.size >= 6){

                    if (this::menu.isInitialized){
                        val menuMulai = menu.findItem(R.id.menu_start_event)
                        menuMulai.isVisible = true
                        invalidateOptionsMenu()
                    }
                    Toast.makeText(this, "Perlombaan Sudah Siap Dimulai", Toast.LENGTH_SHORT).show()
                } else {
                    bind.btnEventEdit.text = "Tambah Peserta"
                }
            } else bind.btnEventEdit.text = "Tambah Peserta"
        } else if (statusEvent == "LOMBA"){
            bind.btnEventEdit.visibility = View.GONE
            bind.txtEventStatus.text = "Event Sedang Berlangsung"
            bind.eventName.setTextColor(Color.GREEN)
            if (participantSize == participantHasBeenCount && participantSize != 0){
                if (this::menu.isInitialized){
                    val menuFinish = menu.findItem(R.id.menu_finish_event)
                    menuFinish.isVisible = true
                }
            }

        } else bind.txtEventStatus.text = "Event Telah Selesai"
    }

    private fun setParticipantEvent(
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
                    viewModel.getParticipant(eventKey).removeValue()
                    if (choosenParticipant.isNotEmpty()){
                        val listChoosenParticipant = HashMap<String, Participant>()
                        for (index in choosenParticipant.indices){
                            listChoosenParticipant[choosenKey[index]] = choosenParticipant[index]

//                            database.child("events/${event.name}/participant/${choosenKey[index]}")
//                                .setValue(choosenParticipant[index])
                            //Simpan Data Participant
                        }
                        database.child("events/${eventKey}/participant")
                            .setValue(listChoosenParticipant)
                    }
                    database.child("events/${eventKey}/totalParticipant")
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
                topsis = Topsis(ahp.getPrioritySum(), resultArray)
                val resultTopsis = topsis.getPreference()
                val resultNotCount = ArrayList<SortedResult>()

                for ((index, item) in result.keys.withIndex()){
                    val resultItem = result[item]
                    if (resultItem?.at == 0.0 && resultItem.ab == 0.0 && resultItem.top == 0.0 && resultItem.bonus == 0.0){
                        Log.d("SETPOST", "CHECKNULLDATA")
                        notCountKeys.add(resultParticipantKey[index])
                        resultNotCount.add(SortedResult(item, resultTopsis[index], result[item],
                            resultWall1[index], resultWall2[index], resultWall3[index], resultWall4[index], null, null, ""))
                    } else if (event.round == "Kualifikasi") {
                        sortedResult.add(SortedResult(item, resultTopsis[index], result[item],
                            resultWall1[index], resultWall2[index], resultWall3[index], resultWall4[index], null, null, resultParticipantKey[index], ArrayList(), gradedOrderList[index]))
                    } else {
                        sortedResult.add(SortedResult(item, resultTopsis[index], result[item],
                            resultWall1[index], resultWall2[index], resultWall3[index], resultWall4[index], resultWall5[index], null, resultParticipantKey[index], ArrayList(), gradedOrderList[index]))
                    }
                    sortedResultSend.add(SortedResult(item, resultTopsis[index], result[item],
                        resultWall1[index], resultWall2[index], resultWall3[index], resultWall4[index], resultWall5[index], null, resultParticipantKey[index], ArrayList(), gradedOrderList[index]))
                }

                sortedResult.sortByDescending { it.preferenceValue }
                sortedResult.addAll(resultNotCount)
//                sortedResultSend= sortedResult
                resultArray.clear()

                if (firstOpen == 0){
                    Log.d("SETPOST", "CHECK")
                    prepareDataToShow()
                } else if (sortedResult.size != oldList.size && sortedResult[0].key != oldList[0].key &&
                    sortedResult[sortedResult.size - 1].key != oldList[oldList.size - 1].key){
                    prepareDataToShow()
                }

                firstOpen++
                swipeRefreshLayout.isRefreshing = false
//
//                if (notCountKeys.size == 0 && statusEvent == "PERLOMBAAN"){
//
//                }
            }
        }
        Toast.makeText(this, "View Tampil", Toast.LENGTH_SHORT).show()
        showView()
    }

    private fun prepareDataToShow() {
        var position = 1
        var setPosition = position

        wallHasBeenCount.forEach { key, list ->
            sortedResult.forEachIndexed{ index, item ->
                if (key == item.key){
                    sortedResult[index].listOfWall = list
                }
            }
        }

        sortedResult.forEachIndexed { index, item ->
            if (index == 0){
                setParticipantPosition(setPosition, index, item.key)
                position++
            } else if (sortedResult[index].preferenceValue == sortedResult[index - 1].preferenceValue){
                setParticipantPosition(setPosition, index, item.key)
                position++
            } else{
                setPosition = position
                setParticipantPosition(setPosition, index, item.key)
                position++
            }
            Log.d("PREPARE", item.position.toString())
        }

        sortedResult.forEachIndexed { index, items ->
            if (index != (sortedResult.size - 1)){
                if (items.order != -1){
                    if (items.preferenceValue == sortedResult[index+1].preferenceValue){
                        if (items.order > sortedResult[index+1].order){
                            if (sortedResult[index+1].order != -1){
                                val before = items
                                sortedResult[index] = sortedResult[index+1]
                                sortedResult[index+1] = before
                            }
                        }
                    }
                }
            }
        }
        adapter.addAll(sortedResult)

    }

    private fun setParticipantPosition(position: Int, index: Int, key: String) {
        sortedResult[index].position = position
        for (item in notCountKeys){
            Log.d("SETPARTPOST", item + " " + sortedResult[index].key)
            if (item == sortedResult[index].key){
                Log.d("SETPARTPOST", "CHECK")
                return
            }
        }
        if (sortedResult[index].listOfWall.size != 0){
            database.child("result/$eventKey/$key/position").setValue(position).addOnSuccessListener {
                Log.d("setParticipantPosition", "SUCCESS")
            }
        }
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
                    val topsisData = DataTopsis(topsis.getNormalization(),
                        topsis.getPriorityNormalize(), topsis.getdPosNeg("+"), topsis.getdPosNeg("-"))
                    intent.putExtra(TopsisActivity.EXTRA_EVENT, sortedResultSend)
                    intent.putExtra(TopsisActivity.EXTRA_TOPSIS, topsisData)
                    startActivity(intent)
                }
                true
            }
            R.id.menu_hapus_event -> {
                MaterialAlertDialogBuilder(this)
                    .setMessage("Apakah Anda Yakin Ingin Menghapus Ini?")
                    .setPositiveButton("Ya"){_,_ ->
                        database.child("events/$eventKey").removeValue().addOnSuccessListener {
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
                        bind.eventNameEdit.visibility = View.VISIBLE
                        bind.eventDateEdit.visibility = View.VISIBLE
                        bind.eventTimeEdit.visibility = View.VISIBLE
                    } else {
                        bind.eventNameEdit.visibility = View.GONE
                        bind.eventDateEdit.visibility = View.GONE
                        bind.eventTimeEdit.visibility = View.GONE
                    }
                }
                true
            }
            R.id.menu_start_event -> {
                if (participantList.size > 20 && judgesList.size < 5){
                    Toast.makeText(this, "Jumlah Juri Kurang. Mohon Ditambah", Toast.LENGTH_SHORT).show()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Apakah Lomba Siap Dimulai")
                        .setMessage("Jumlah Peserta" + participantList.size.toString())
                        .setPositiveButton("Ya"){_,_ ->
                            val round = if (participantList.size <= 6){
                                "Final"
                            } else if (participantList.size <= 20) {
                                "Semi Final"
                            } else "Kualifikasi"

                            database.child("events/$eventKey/round").setValue(round).addOnSuccessListener {
                                database.child("events/$eventKey/status").setValue("LOMBA").addOnSuccessListener {
                                    checkEventStatus()
                                }
                            }
                        }
                        .show()
                }

                true
            }
            R.id.menu_hapus_peserta -> {
                setParticipantEvent(choosenParticipant, choosenKey)
                true
            }
            R.id.menu_nama_juri_lapangan -> {
                var message = ""
                if (judgesList.size != 0){
                    judgesList.forEachIndexed { index, s ->
                        message = message + "Dinding ${index + 1} = " + s + "\n"
                    }
                } else message = "Juri lapangan belum dipilih"


                val builder = MaterialAlertDialogBuilder(this)
                    .setTitle("Juri Lapangan yang bertugas:")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ ->
                    }
                if (message == "Juri lapangan belum dipilih"){
                    builder.setNeutralButton("TAMBAH") { _,_ ->
                            val intent = Intent(this, AddJudgesActivity::class.java)
                            intent.putExtra("EXTRA_EVENT_KEY", eventKey)
                            intent.putExtra("EXTRA_EVENT", event)
                            startActivity(intent)
                            true
                    }
                } else {
                    builder.setNeutralButton("UBAH") { _,_ ->
                        val intent = Intent(this, AddJudgesActivity::class.java)
                        intent.putExtra("EXTRA_EVENT_KEY", eventKey)
                        intent.putExtra("EXTRA_EVENT", event)
                        intent.putExtra("EXTRA_STATUS", "EDIT")
                        startActivity(intent)
                        true
                    }
                }

                builder.show()
                true
            }
            R.id.menu_print_hasil -> {
                val editText = EditText(this)
                editText.setText(event.name)
                MaterialAlertDialogBuilder(this)
                    .setTitle("Masukkan Nama File")
                    .setView(editText)
                    .setPositiveButton("OK") { _, _ ->
                        Log.d("EPERMISS", checkPermission().toString())
                        if (checkPermission()){
                            if (editText.text.isNullOrEmpty()){
                                print("workbook")
                            } else print(editText.text.toString())
                        } else requestPermission()
                    }
                    .show()
                true
            }
            R.id.menu_tambah_peserta ->{
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_finish_event -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Apakah Anda Yakin Ingin Menyelesaikan Perlombaan?")
                    .setMessage("")
                    .setPositiveButton("YA") { _, _ ->
                        setNextEvent()
                    }
                    .setNegativeButton("TIDAK") { _, _ ->

                    }
                    .show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setNextEvent() {
        if (event.round != "Final"){
            var participantListSize = 0
            val nextRound: String
            if (event.round == "Kualifikasi"){
                nextRound = "Semi Final"
                participantListSize = 20
            } else {
                nextRound = "Final"
                participantListSize = 6
            }

            val nextEventParticipant = HashMap<String, Participant>()

            sortedResultSend.forEachIndexed { index, item ->
                if (item.position!! <= participantListSize){
                    event.participant?.forEach { s, participant ->
                        if (s == sortedResultSend[index].key){
                            nextEventParticipant[sortedResultSend[index].key] = participant
                        }
                    }
                }
            }

            val dateStr = bind.eventDate.text.toString().trim()
            val l = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm"))
            LocalDateTime.from(l).plusDays(1)

            val date = l.toInstant(ZoneId.systemDefault().rules.getOffset(l)).toEpochMilli()

            val newEvent = Event(event.name, date, event.location, false,
                nextEventParticipant.size, event.president, "PERSIAPAN", null,
                nextEventParticipant, event.coordinator, nextRound)

            val key = database.child("events").push().key.toString()
            database.child("events").child(key).setValue(event).addOnSuccessListener{
                database.child("events/$eventKey/finished").setValue(true).addOnSuccessListener {
                    Toast.makeText(this, "Event Telah Selesai", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            database.child("events/$eventKey/finished").setValue(true).addOnSuccessListener {
                Toast.makeText(this, "Event Telah Selesai", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun print(fileName: String) {
        val printResult = sortedResultSend
        printResult.sortByDescending { it.preferenceValue }
        val printData = PrintData(printResult)
        try {
            printData.print(fileName)
            Toast.makeText(this, "File Berhasil Disimpan", Toast.LENGTH_SHORT).show()
        } catch (e: Exception){
            Toast.makeText(this, "File Gagal Disimpan", Toast.LENGTH_SHORT).show()
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

    private fun showMenuOptions() {
        if (this::menu.isInitialized){

            val menuAhp = menu.findItem(R.id.menu_ahp)
            val menuTopsis = menu.findItem(R.id.menu_topsis)

            val menuUbah = menu.findItem(R.id.menu_edit_event)
            val menuHapus = menu.findItem(R.id.menu_hapus_event)
            val menuMulai = menu.findItem(R.id.menu_start_event)
            val menuStop = menu.findItem(R.id.menu_finish_event)
            val menuHapusPeserta = menu.findItem(R.id.menu_hapus_peserta)
            val menuTambahPeserta = menu.findItem(R.id.menu_tambah_peserta)
            val menuJuriLapangan = menu.findItem(R.id.menu_nama_juri_lapangan)
            val menuPrint = menu.findItem(R.id.menu_print_hasil)

            //change to admin
            if (userType != "Presiden Juri") {
                menuAhp?.isVisible = false
                menuTopsis?.isVisible = false
            }

            if (userType != "Panitia"){
                menuUbah?.isVisible = false
                menuHapus?.isVisible = false
            }

            if (event.status == "PERSIAPAN"){
                menuHapusPeserta.isVisible = false
            }

            menuMulai?.isVisible = false
            menuStop?.isVisible = false
            menuPrint.isVisible = false

            if (userType != "Presiden Juri"){
                menuJuriLapangan?.isVisible = false
                menuHapusPeserta?.isVisible = false
                menuTambahPeserta?.isVisible = false
            }

            if (userType == "Presiden Juri"){
                menuUbah?.isVisible = true
                menuHapus?.isVisible = true
            }

            if (userType == "Presiden Juri" || userType == "Panitia"){
                val size = event.participant?.size
                if (size != null){
                    if (size > 0){
                        menuPrint.isVisible = true
                    }
                }
            }
            Log.d("EventNull", event.toString())

            if (event.judges != null){
                if (event.judges!!.size >= 4 && participantList.size >= 6) {
                    menuMulai.isVisible = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var bool = false
        this.menu = menu
        if(event.status != null){
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.event_menu, menu)
            bool = true
        }
        return bool
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private var activityResult = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK){
            checkEventParticipant()
        }
    }
}