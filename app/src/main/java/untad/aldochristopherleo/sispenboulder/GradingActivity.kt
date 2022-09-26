package untad.aldochristopherleo.sispenboulder

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

class GradingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    private var _bind : ActivityGradingBinding? = null
    private val bind get() = _bind!!
    private var topValue = 0
    private var atValue = 0
    private var bonusValue = 0
    private var abValue = 0

    private val viewModel: MainViewModel by viewModels()

    private lateinit var stringArray: Array<String>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _bind = ActivityGradingBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val event = if (Build.VERSION.SDK_INT >= 33){
            intent.getParcelableExtra("EXTRA_EVENT", Event::class.java)
        } else {
            intent.getParcelableExtra<Event>("EXTRA_EVENT") as Event
        }
//        val event = intent.getParcelableExtra<Event>("EXTRA_EVENT") as Event
        database = Firebase.database.reference

        bind.layoutHide.visibility = View.GONE
        bind.btnConfirmGrading.visibility = View.GONE

        if (event != null) {
            viewModel.getParticipant(event .name).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arrayList = ArrayList<String>()
                    for (item in snapshot.children){
                        arrayList.add(item.child("name").value.toString())
                    }
                    val checkList = ArrayList<String>()
                    database.child("result/${event.name}").get().addOnSuccessListener {
                        for (index in arrayList.indices){
                            if (!it.hasChild(arrayList[index])){
                                checkList.add(arrayList[index])
                            }
                        }
                        if (checkList.size == 0){
                            closeDialog()
                        } else populateSpinner(checkList)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "loadPost:onCancelled", error.toException())
                }
            })
        }

        bind.btnTopMinus.setOnClickListener { setTopValue(it) }
        bind.btnTopPlus.setOnClickListener { setTopValue(it) }
        bind.btnAtMinus.setOnClickListener { setAtValue(it) }
        bind.btnAtPlus.setOnClickListener { setAtValue(it) }
        bind.btnBonusMinus.setOnClickListener { setBonusValue(it) }
        bind.btnBonusPlus.setOnClickListener { setBonusValue(it) }
        bind.btnAbMinus.setOnClickListener { setAbValue(it) }
        bind.btnAbPlus.setOnClickListener { setAbValue(it) }
        bind.btnConfirmGrading.setOnClickListener {
            val participant = bind.txtParticipantChoose.editText?.text.toString()
            MaterialAlertDialogBuilder(this)
                .setTitle("Apakah Data Yang Dimasukkan Sudah Benar?")
                .setMessage("Peserta yang dinilai : $participant")
                .setNegativeButton("Tidak"){ dialog, which ->
                }
                .setPositiveButton("Yakin"){ dialog, which ->
                    if (event != null) {
                        setResult(event, participant)
                    }
                }
                .show()
        }
    }

    private fun setResult(event: Event, participant: String) {
        val result = Result(topValue.toDouble(), atValue.toDouble(), bonusValue.toDouble(), abValue.toDouble())
        database.child("result/${event.name}/$participant").setValue(result).addOnSuccessListener {
            finish()
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

        val adapter = ArrayAdapter(this, R.layout.list_type, stringArray)

        val spinner = bind.txtParticipantChoose.editText as? AutoCompleteTextView
        spinner?.setAdapter(adapter)
        spinner?.setOnItemClickListener { adapterView, view, i, l ->
            bind.txtParticipantChoose.isEnabled = false
            bind.layoutHide.visibility = View.VISIBLE
            bind.btnConfirmGrading.visibility = View.VISIBLE
        }
    }
}