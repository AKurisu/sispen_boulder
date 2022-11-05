package untad.aldochristopherleo.sispenboulder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.Result
import untad.aldochristopherleo.sispenboulder.databinding.ActivityGradingBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

@Suppress("DEPRECATION")
class GradingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
        const val EXTRA_EVENT_KEY = "extra_event_key"
        const val EXTRA_NAME = "extra_name"
    }

    private var _bind : ActivityGradingBinding? = null
    private val bind get() = _bind!!
    private var topValue = 0
    private var atValue = 0
    private var bonusValue = 0
    private var abValue = 0
    private var dialogOpen = 0

    private val viewModel: MainViewModel by viewModels()

    private lateinit var total: Result
    private lateinit var event: Event
    private lateinit var userWallKey: String
    private lateinit var userName: String
    private lateinit var wallList : Array<String>
    private lateinit var eventKey: String
    private lateinit var selectedKeyParticipant: String
    private lateinit var keyParticipant: ArrayList<String>
    private lateinit var stringArray: Array<String>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _bind = ActivityGradingBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.layoutHide.visibility = View.GONE
        bind.btnConfirmGrading.visibility = View.GONE

        eventKey = intent.getStringExtra(EXTRA_EVENT_KEY).toString()
        userName = intent.getStringExtra(EXTRA_NAME).toString()
        userWallKey = ""
        if (Build.VERSION.SDK_INT >= 33){
            event = intent.getParcelableExtra(EXTRA_EVENT, Event::class.java)!!
        } else {
            event = intent.getParcelableExtra<Event>(EXTRA_EVENT) as Event
        }
//        val event = intent.getParcelableExtra<Event>("EXTRA_EVENT") as Event
        database = Firebase.database.reference

        val listOfWalls = ArrayList<String>()
        event.judges?.forEach { (key, value) ->
            if (value.name == userName){
                  listOfWalls.add(key)
            }
        }
        wallList = listOfWalls.toTypedArray()
        if (wallList.size > 1) {
             userWallKey = pickDialog()
        } else userWallKey = wallList[0]

        if (userWallKey.isNullOrEmpty()){
            MaterialAlertDialogBuilder(this)
                .setTitle("Event Bermasalah.")
                .setMessage("Mohon Laporkan Kepada Administrator")
                .setPositiveButton("Ok"){_,_ ->
                    finish()
                }
                .show()
        } else setTitle(userWallKey)

        selectedKeyParticipant = ""

        if (event != null) {
            viewModel.getParticipant(eventKey).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nameList = ArrayList<String>()
                    val keyList = ArrayList<String>()
                    for (item in snapshot.children){
                        nameList.add(item.child("name").value.toString())
                        item.key?.let {
                            keyList.add(it)
                        }
                    }
                    val checkList = ArrayList<String>()
                    keyParticipant = ArrayList()
                    database.child("result/$eventKey").get().addOnSuccessListener {
                        for (index in keyList.indices){
                            if (!it.hasChild(keyList[index])){
                                checkList.add(nameList[index])
                                keyParticipant.add(keyList[index])
                            }
                        }
                        if (checkList.size == 0 && nameList.size == 0){
                            closeDialog()
                        } else {
                            populateSpinner(checkList)
                            total = Result()
                            setResultValue()
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())
                }
            })
        }

        setValue()


        bind.btnConfirmGrading.setOnClickListener {
            val participant = bind.txtParticipantChoose.editText?.text.toString()
            MaterialAlertDialogBuilder(this)
                .setTitle("Apakah Data Yang Dimasukkan Sudah Benar?")
                .setMessage("Peserta yang dinilai : $participant")
                .setNegativeButton("Tidak"){ _, _ -> }
                .setPositiveButton("Yakin"){ _, _ ->
                    if (event != null) {
                        setResult(participant)
                    }
                }
                .show()
        }
    }

    private fun pickDialog(): String {
        var result = ""

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Silahkan Pilih Dinding")
            .setSingleChoiceItems(wallList,-1) { _, which ->
                result = wallList[which]
            }
        if (dialogOpen != 0){
            builder.setNeutralButton("Cancel"){dialog,_ -> dialog.cancel()}
        }
        dialogOpen++
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        return result
    }

    private fun setResultValue() {
        database.child("result/$eventKey/$selectedKeyParticipant/Total").get().addOnSuccessListener {
            if (it.value != null){
                total = it.getValue(Result::class.java)!!
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, total.ab.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setValue() {
        bind.btnTopMinus.setOnClickListener { setTopValue(it) }
        bind.btnTopPlus.setOnClickListener { setTopValue(it) }
        bind.btnAtMinus.setOnClickListener { setAtValue(it) }
        bind.btnAtPlus.setOnClickListener { setAtValue(it) }
        bind.btnBonusMinus.setOnClickListener { setBonusValue(it) }
        bind.btnBonusPlus.setOnClickListener { setBonusValue(it) }
        bind.btnAbMinus.setOnClickListener { setAbValue(it) }
        bind.btnAbPlus.setOnClickListener { setAbValue(it) }
    }

    private fun setResult(participant: String) {
        val result = Result(topValue.toDouble(), atValue.toDouble(), bonusValue.toDouble(), abValue.toDouble())
        val resultTotal =
            Result(result.top + total.top, result.at + total.at, result.bonus + total.bonus, result.ab + total.ab)
        database.child("result/$eventKey/$selectedKeyParticipant/$userWallKey").setValue(result).addOnSuccessListener {
            database.child("result/$eventKey/$selectedKeyParticipant/Total").setValue(resultTotal).addOnSuccessListener {
                Toast.makeText(this, "Data Berhasil Diinput", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun closeDialog(){
        MaterialAlertDialogBuilder(this).setTitle("Tidak Ada Peserta")
            .setMessage("Semua Peserta Telah Dinilai")
            .setPositiveButton("Kembali"){ _, _ ->
                finish()
            }
            .show()
    }

    private fun setAbValue(view: View) {
        if (view.id == R.id.btn_abMinus){
            abValue -= 1
        } else if (view.id == R.id.btn_abPlus){
            abValue += 1
        }
        abValue = checkValue(abValue)
        bind.txtAb.text = abValue.toString()
    }

    private fun setBonusValue(view: View) {
        if (view.id == R.id.btn_bonusMinus){
            bonusValue -= 1
        } else if (view.id == R.id.btn_bonusPlus){
            bonusValue += 1
        }
        bonusValue = checkValue(bonusValue)
        bind.txtBonus.text = bonusValue.toString()
    }

    private fun setAtValue(view: View) {
        if (view.id == R.id.btn_atMinus){
            atValue -= 1
        } else if (view.id == R.id.btn_atPlus){
            atValue += 1
        }
        atValue = checkValue(atValue)
        bind.txtAt.text = atValue.toString()
    }

    private fun setTopValue(view: View) {
        if (view.id == R.id.btn_topMinus){
            topValue -= 1
        } else if (view.id == R.id.btn_topPlus){
            topValue += 1
        }
        topValue = checkValue(topValue)
        bind.txtTop.text = topValue.toString()
    }

    private fun checkValue(value: Int) : Int{
        var returnValue = value
        if (value < 0) {
            returnValue = 0
        } else if (value > 9){
            returnValue = 9
        }
        return returnValue
    }

    private fun populateSpinner(allParticipant: ArrayList<String>) {
        stringArray = allParticipant.toArray(arrayOfNulls<String>(allParticipant.size))

        Toast.makeText(this@GradingActivity, stringArray.size.toString(), Toast.LENGTH_SHORT).show()

        val adapter = ArrayAdapter(this, R.layout.list_type, stringArray)

        val spinner = bind.txtParticipantChoose.editText as? AutoCompleteTextView
        spinner?.setAdapter(adapter)
        spinner?.setOnItemClickListener { _, _, pos, _ ->
            selectedKeyParticipant = keyParticipant[pos]
            bind.txtParticipantChoose.isEnabled = false
            bind.layoutHide.visibility = View.VISIBLE
            bind.btnConfirmGrading.visibility = View.VISIBLE
            total = Result()

            setResultValue()
        }
    }
}