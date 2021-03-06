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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        _bind = ActivityEventBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("Detail Event")

        bind.layoutHide.visibility = View.GONE
        bind.progressBar.visibility = View.VISIBLE

        event = intent.getParcelableExtra<Event>(EXTRA_EVENT) as Event
        val participantList = ArrayList<Alternative>()
        val allParticipantList = ArrayList<String>()
        val allParticipant = ArrayList<Participant>()
        val allParticipantKey = ArrayList<String>()
        val checkedItems = ArrayList<Boolean>()
        var choosenParticipant = ArrayList<Participant>()
        var choosenKey = ArrayList<String>()

        result = LinkedHashMap()
        sortedResult = ArrayList()
        sortedResultSend = ArrayList()
        resultArray = ArrayList()

        database = Firebase.database.reference

        val rv = bind.participantRv
        adapter = ListParticipantAdapter(sortedResult)
        viewModel.getParticipant(event.name).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                participantList.clear()
                for (item in snapshot.children) {
                    val user = item.getValue(Participant::class.java)
                    participantList.add(Alternative(user?.name))

                    viewModel.getResult(event.name).get().addOnSuccessListener {
                        var data = Result()
                        if(it.hasChild(user?.name!!)){
                            result.put(user.name,
                                it.child(user.name).getValue(Result::class.java)!!
                            )
                            data = it.child(user.name).getValue(Result::class.java)!!
                        } else result.put(user.name, Result())

                        resultArray.addAll(data.toArrayList())

                        setPosition(snapshot.childrenCount)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "loadPost:onCancelled", error.toException())
            }
        })

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        viewModel.getAllParticipants().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val nama = item.child("name").value.toString()
                    var compareList = false
                    allParticipantKey.add(item.key.toString())
                    allParticipant.add(Participant(nama))
                    allParticipantList.add(nama)
                    for (itemlist in participantList){
                        if (itemlist.name == nama){
                            choosenParticipant.add(Participant(nama))
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
            bind.btnEventEdit.text = if (it.type == "Manajer")
                "Tambah Peserta" else "Nilai Peserta"
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
            if (userType == "Manajer"){
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
                        for (index in choosenParticipant.indices){
                            database.child("events/${event.name}/participant/${choosenKey[index]}")
                                .setValue(choosenParticipant[index])
                        }
                        database.child("events/${event.name}/totalParticipant")
                            .setValue(choosenParticipant.size)
                        adapter.notifyDataSetChanged()
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
            } else {
                val intent = Intent(this, GradingActivity::class.java)
                intent.putExtra(GradingActivity.EXTRA_EVENT, event)
                startActivity(intent)
            }
        }
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
                    sortedResult.add(SortedResult(item, resultTopsis[index], result[item]))
                    sortedResultSend.add(SortedResult(item, resultTopsis[index], result[item]))
                    index++
                }

                sortedResult.sortByDescending { it.preferenceValue }

                adapter.notifyDataSetChanged()
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