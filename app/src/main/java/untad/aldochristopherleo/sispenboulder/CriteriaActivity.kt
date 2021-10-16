package untad.aldochristopherleo.sispenboulder

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import untad.aldochristopherleo.sispenboulder.databinding.ActivityCriteriaBinding
import untad.aldochristopherleo.sispenboulder.util.Ahp
import untad.aldochristopherleo.sispenboulder.util.ChangedListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CriteriaActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCriteriaBinding
    private lateinit var database: DatabaseReference
    private val orderedList = ArrayList<EditText>()
    private val valueList = ArrayList<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCriteriaBinding.inflate(layoutInflater)
        setContentView(bind.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        for (index in 1..12) {
            valueList.add(0.0)
        }
        println(valueList)


        val data = HashMap<String, Double>()
        val value = ArrayList<String>()
        val defineInputVar = defineInput()

        for (editText in defineInput()){
            changeListener(editText)
        }

        database = Firebase.database.reference


        database.child("algorithm").child("ahp")
            .child("criteria").get().addOnSuccessListener {
            if (it.exists()){
                println("CEK")
                for (item in it.children){
                    Collections.addAll(value, item.value.toString())
                }
                orderedList.indices.forEach { index ->
                    orderedList[index].setText(value[index])
                    orderedList[index].isEnabled = value[index].toDouble() >= 1.0
                }
                getCriteriaResult()
            }
        }

        bind.btnConfirmCriteria.setOnClickListener {
            for ((index, item) in orderedList.withIndex()){
                if (item.text.isNullOrBlank()){
                    Toast.makeText(this, "Lengkapi data anda", Toast.LENGTH_SHORT).show()
                    break
                } else {
//                    var id = item.resources.getResourceName(item.id)
//                    id = id.replace("untad.aldochristopherleo.sispenboulder:id/","")
                    if (index < 10){
                        data.put("v0$index", item.text.toString().toDouble())
                    } else data.put("v$index", item.text.toString().toDouble())

                    if ((index + 1 ) == orderedList.size){
                        sendData(data)
                    }
                }
            }
        }
    }

    private fun getCriteriaResult() {

        var editFill = 0
        for (index in orderedList.indices){
            if (orderedList[index].text.isNullOrBlank()){
                editFill = 0
            } else {
                editFill += 1
                valueList[index] = orderedList[index].text.toString().toDouble()
                println(valueList)
            }
        }

        if (editFill == 12){
            val resultObject = Ahp(valueList)
            val result = BigDecimal(resultObject.getResults(3)).setScale(2, RoundingMode.HALF_EVEN)
            var total = 0.0
            for (value in valueList){
                total += value
            }
            bind.txtCriteriaResult.text = result.toPlainString()
            if (result > BigDecimal(0.1)){
                bind.txtCriteriaResult.setTextColor(Color.parseColor("#EC7063"))
            } else bind.txtCriteriaResult.setTextColor(Color.parseColor("#5DADE2"))
        }
    }

    private fun sendData(data: HashMap<String, Double>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Apakah Anda Yakin Telah Benar?")
            .setPositiveButton("Ya"){ _, _ ->
                database.child("algorithm").child("ahp").child("criteria").setValue(data)
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Tidak"){ _, _ ->
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun changeListener(editText: EditText){
        val list = defineInput()
        var watchIndex = 0
        editText.setOnFocusChangeListener{ view, b ->
            if (b){
                for (item in list.iterator()){
                    val index = list.indexOf(item)
                    if (index % 2 == 0){
                        item.removeTextChangedListener(ChangedListener(list[index + 1], item))
                        if (item == editText) watchIndex = index + 1
                    } else {
                        item.removeTextChangedListener(ChangedListener(list[index - 1], item))
                        if (item == editText) watchIndex = index - 1
                    }
                }
                editText.addTextChangedListener(ChangedListener(list[watchIndex], editText))
                getCriteriaResult()
            }
        }
    }

    private fun defineInput(): ArrayList<EditText>{
        val top1 = bind.alt11
        val top2 = bind.alt21
        val top3 = bind.alt31
        val at1 = bind.alt12
        val at2 = bind.alt42
        val at3 = bind.alt61
        val bonus1 = bind.alt22
        val bonus2 = bind.alt41
        val bonus3 = bind.alt51
        val ab1 = bind.alt32
        val ab2 = bind.alt52
        val ab3 = bind.alt62
        val list = ArrayList<EditText>()
        if (orderedList.isEmpty()){
            Collections.addAll(orderedList, top1, top2, top3, at1, at2, at3, bonus1, bonus2, bonus3, ab1, ab3, ab2)
        }
        Log.d("Criteria", orderedList.size.toString())
        Collections.addAll(list, top1, at1, top2, bonus1, top3, ab1, bonus2, at2, bonus3, ab2, at3, ab3)
        return list
    }
}