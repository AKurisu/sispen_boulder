package untad.aldochristopherleo.sispenboulder.util

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.math.RoundingMode

class ChangedListener internal constructor(private val editText: EditText): TextWatcher{

    override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
    }
    override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
        val changeValue = (start.toString() + before.toString() + count.toString())
        val textInput = p0?.trim()
        Log.d("cek", textInput.toString())
        if (changeValue != "010"){
            try {
                textInput.toString().toDouble()
                if (textInput.toString().toDouble() < 1){
                    return
                } else {
                    editText.isEnabled = false
                    val value = BigDecimal(1 / textInput.toString().toDouble()).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
                    editText.setText(value)
                }
            } catch (e: NumberFormatException){
                Log.d("tag", e.toString())
            }

        } else {
            editText.text.clear()
            editText.isEnabled = true
        }
    }
    override fun afterTextChanged(p0: Editable?) {
    }
}