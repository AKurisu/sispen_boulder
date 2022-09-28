package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.adapter.ListParticipantAdapter
import untad.aldochristopherleo.sispenboulder.data.*
import untad.aldochristopherleo.sispenboulder.databinding.ActivityEventBinding
import untad.aldochristopherleo.sispenboulder.util.Ahp
import untad.aldochristopherleo.sispenboulder.util.DateConverter
import untad.aldochristopherleo.sispenboulder.util.MainViewModel
import untad.aldochristopherleo.sispenboulder.util.Topsis
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class EventActivity : AppCompatActivity() {

    init {

    }

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    var userType : String? = null
    private var _bind : ActivityEventBinding? = null
    private val bind get() = _bind!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var result : LinkedHashMap<String, Result>
    private lateinit var booleanArray: BooleanArray
    private lateinit var stringArray: Array<String>
    private lateinit var resultArray: ArrayList<Double>
    private lateinit var sortedResult: ArrayList<SortedResult>
    private lateinit var sortedResultSend: ArrayList<SortedResult>
    private lateinit var database: DatabaseReference
    private lateinit var adapter : ListParticipantAdapter
    private lateinit var event : Event
    private lateinit var topsis : Topsis
    private lateinit var allParticipant : ArrayList<Participant>
    private lateinit var allParticipantKey : ArrayList<String>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var refreshListener: SwipeRefreshLayout.OnRefreshListener
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
        setTitle("Detail Event")

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

        database = Firebase.database.reference

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
                populateSpinner(allParticipantList, checkedItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }

        })

        viewModel.user.observe(this) {
            if (it.type == "Manajer") bind.btnEventEdit.visibility = View.GONE
            bind.btnEventEdit.text = if (it.type == "Panitia")
                "Tambah Peserta" else if (it.type == "Juri Lapangan")
                    "Nilai Peserta" else "Pilih Juri Lapangan"
            userType = it.type
        }

        if (event.finished){
            bind.btnEventEdit.visibility = View.GONE
        }

        bind.eventName.text = event.name
        val date = DateConverter(event.date)
        bind.eventDate.text = date.getDetailDateTime()
        bind.eventLocation.text = event.location
        bind.eventTotalParticipant.text = event.totalParticipant.toString()

        bind.btnEventEdit.setOnClickListener{
            if (userType == "Panitia"){
                setButtonAction(choosenParticipant, choosenKey)
            } else if (userType == "Juri Lapangan"){
                val intent = Intent(this, GradingActivity::class.java)
                intent.putExtra(GradingActivity.EXTRA_EVENT, event)
                startActivity(intent)
            } else if (userType == "Presiden Juri"){
                val intent = Intent(this, AddJudgesActivity::class.java)
                intent.putExtra("EXTRA_EVENT", event)
            }
        }

        swipeRefreshLayout.setOnRefreshListener(refreshListener)

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
                        participantList.add(Alternative(participant?.name))

                        viewModel.getResult(event.name).get().addOnSuccessListener {
                            var data = Result()
                            if(it.hasChild(participant?.name!!)){
                                result.put(participant.name,
                                    it.child(participant.name).getValue(Result::class.java)!!
                                )
                                data = it.child(participant.name).getValue(Result::class.java)!!
                            } else result.put(participant.name, Result())

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

    private fun checkEventStatus(): String{
        var status = "NULL"
        database.child("events/${event.name}/status").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    status = snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("STATUS", error.toString())
                }

            }
        )
        return status
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
                .setNeutralButton("Cancel") { dialog, which ->
                    choosenParticipant = saveChoosenParticipant
                    choosenKey = saveChoosenKey
                }
                .setPositiveButton("OK") { dialog, which ->
                    viewModel.getParticipant(event.name).removeValue()
                    if (choosenParticipant.isNotEmpty()){
                        for (index in choosenParticipant.indices){
                            database.child("events/${event.name}/participant/${choosenKey[index]}")
                                .setValue(choosenParticipant[index])
                        }
                    }
                    database.child("events/${event.name}/totalParticipant")
                        .setValue(choosenParticipant.size)


                    swipeRefreshLayout.setOnRefreshListener(refreshListener)
                }
                .setMultiChoiceItems(stringArray, booleanArray) { dialog, which, checked ->
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
                var index = 0

                for (item in result.keys){
                    Log.d("CHECK", result.size.toString())
                    sortedResult.add(SortedResult(item, resultTopsis[index], result[item]))
                    index++
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateSpinner(allParticipant: ArrayList<String>, checkedItems: ArrayList<Boolean>) {
        stringArray = allParticipant.toArray(arrayOfNulls<String>(allParticipant.size))

        booleanArray = BooleanArray(checkedItems.size)
        for (index in checkedItems.indices){
            booleanArray[index] = checkedItems[index]
        }

        bind.layoutHide.visibility = View.VISIBLE
        bind.progressBar.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var bool = false
        if(event.totalParticipant > 0){
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.event_menu, menu)
            bool = true
        }
        return bool
    }
}