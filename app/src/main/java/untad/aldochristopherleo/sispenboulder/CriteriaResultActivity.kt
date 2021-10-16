package untad.aldochristopherleo.sispenboulder

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import untad.aldochristopherleo.sispenboulder.databinding.ActivityCriteriaBinding
import untad.aldochristopherleo.sispenboulder.databinding.ActivityCriteriaResultBinding
import untad.aldochristopherleo.sispenboulder.util.Ahp
import untad.aldochristopherleo.sispenboulder.util.MainViewModel
import untad.aldochristopherleo.sispenboulder.util.Topsis
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class CriteriaResultActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCriteriaResultBinding
    private var txtBind1 = ArrayList<TextView>()
    private var txtResult1 = ArrayList<TextView>()
    private var txtBind2 = ArrayList<TextView>()
    private var txtResult2 = ArrayList<TextView>()
    private var txtBind3 = ArrayList<TextView>()
    private var txtResult3 = ArrayList<TextView>()

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCriteriaResultBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bindEditText()

        val transition = ChangeBounds()
        transition.duration = 500

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setValue()
        bind.cvrFirstButton.setOnClickListener {
            if (bind.expandCvrFirst.visibility == View.VISIBLE){
                TransitionManager.beginDelayedTransition(bind.cvrFirst, transition)
                bind.expandCvrFirst.visibility = View.GONE
                bind.cvrFirstButton.setIconResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(bind.cvrFirst, transition)
                bind.expandCvrFirst.visibility = View.VISIBLE
                bind.cvrFirstButton.setIconResource(R.drawable.ic_baseline_expand_less_24)
            }
        }
        bind.cvrSecondButton.setOnClickListener {
            if (bind.expandCvrSecond.visibility == View.VISIBLE){
                TransitionManager.beginDelayedTransition(bind.cvrSecond, transition)
                bind.expandCvrSecond.visibility = View.GONE
                bind.cvrSecondButton.setIconResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(bind.cvrSecond, transition)
                bind.expandCvrSecond.visibility = View.VISIBLE
                bind.cvrSecondButton.setIconResource(R.drawable.ic_baseline_expand_less_24)
            }
        }
        bind.cvrThirdButton.setOnClickListener {
            if (bind.expandCvrThird.visibility == View.VISIBLE){
                TransitionManager.beginDelayedTransition(bind.cvrThird, transition)
                bind.expandCvrThird.visibility = View.GONE
                bind.cvrThirdButton.setIconResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(bind.cvrThird, transition)
                bind.expandCvrThird.visibility = View.VISIBLE
                bind.cvrThirdButton.setIconResource(R.drawable.ic_baseline_expand_less_24)
            }
        }
        bind.cvrFourthButton.setOnClickListener {
            if (bind.expandCvrFourth.visibility == View.VISIBLE){
                TransitionManager.beginDelayedTransition(bind.cvrFourth, transition)
                bind.expandCvrFourth.visibility = View.GONE
                bind.cvrFourthButton.setIconResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(bind.cvrFourth, transition)
                bind.expandCvrFourth.visibility = View.VISIBLE
                bind.cvrFourthButton.setIconResource(R.drawable.ic_baseline_expand_less_24)
            }
        }
    }

    private fun setValue() {
        viewModel.ahpPriority.observe(this){ value ->
            val ahp = Ahp(value)
            val firstMatrix = ahp.getFirstMatrix()
            val firstResult = ahp.getFirstSum()
            val normMatrix = ahp.getNormalizeMatrix()
            val normResult = ahp.getNormalizeSum()
            val consMatrix = ahp.getConsistencyMatrix()
            val consResult = ahp.getConsistencySum()

            firstMatrix.indices.forEach { index ->
                txtBind1[index].text = firstMatrix[index].toString()
            }

            firstResult.indices.forEach { index ->
                txtResult1[index].text = firstResult[index].toString()
            }

            normMatrix.indices.forEach { index ->
                txtBind2[index].text = BigDecimal(normMatrix[index]).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
            }

            normResult.indices.forEach { index ->
                txtResult2[index].text = BigDecimal(normResult[index]).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
            }

            consMatrix.indices.forEach { index ->
                txtBind3[index].text = BigDecimal(consMatrix[index]).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
            }

            consResult.indices.forEach { index ->
                txtResult3[index].text = BigDecimal(consResult[index]).setScale(2, RoundingMode.HALF_EVEN).toPlainString()
            }

            bind.cvrFourthResult1.setText(getString(R.string.ahp_ci, BigDecimal(ahp.getResults(1)).setScale(2, RoundingMode.HALF_EVEN).toPlainString()))
            bind.cvrFourthResult2.setText(getString(R.string.ahp_ci, BigDecimal(ahp.getResults(2)).setScale(2, RoundingMode.HALF_EVEN).toPlainString()))
            bind.cvrFourthResult3.setText((getString(R.string.ahp_ci, BigDecimal(ahp.getResults(3)).setScale(2, RoundingMode.HALF_EVEN).toPlainString())))
            val consistency = if (ahp.getResults(3) < 0.1) "Konsisten" else "Tidak Konsisten"
            bind.cvrFourthResult4.text = consistency


            }
        }

    private fun bindEditText(){

        Collections.addAll(txtBind1, bind.criteriaDataFirst1, bind.criteriaDataFirst2, bind.criteriaDataFirst3,
            bind.criteriaDataFirst4, bind.criteriaDataFirst5, bind.criteriaDataFirst6, bind.criteriaDataFirst7,
            bind.criteriaDataFirst8, bind.criteriaDataFirst9, bind.criteriaDataFirst10, bind.criteriaDataFirst11,
            bind.criteriaDataFirst12, bind.criteriaDataFirst13, bind.criteriaDataFirst14, bind.criteriaDataFirst15,
            bind.criteriaDataFirst16)

        Collections.addAll(txtResult1, bind.criteriaResultFirst1, bind.criteriaResultFirst2, bind.criteriaResultFirst3,
            bind.criteriaResultFirst4)

        Collections.addAll(txtBind2, bind.criteriaDataSecond1, bind.criteriaDataSecond2, bind.criteriaDataSecond3,
            bind.criteriaDataSecond4, bind.criteriaDataSecond5, bind.criteriaDataSecond6, bind.criteriaDataSecond7,
            bind.criteriaDataSecond8, bind.criteriaDataSecond9, bind.criteriaDataSecond10, bind.criteriaDataSecond11,
            bind.criteriaDataSecond12, bind.criteriaDataSecond13, bind.criteriaDataSecond14, bind.criteriaDataSecond15,
            bind.criteriaDataSecond16)

        Collections.addAll(txtResult2, bind.criteriaResultSecond1, bind.criteriaResultSecond2, bind.criteriaResultSecond3,
            bind.criteriaResultSecond4, bind.criteriaResultSecond5, bind.criteriaResultSecond6, bind.criteriaResultSecond7,
            bind.criteriaResultSecond8)

        Collections.addAll(txtBind3, bind.criteriaDataThird1, bind.criteriaDataThird2, bind.criteriaDataThird3,
            bind.criteriaDataThird4, bind.criteriaDataThird5, bind.criteriaDataThird6, bind.criteriaDataThird7,
            bind.criteriaDataThird8, bind.criteriaDataThird9, bind.criteriaDataThird10, bind.criteriaDataThird11,
            bind.criteriaDataThird12, bind.criteriaDataThird13, bind.criteriaDataThird14, bind.criteriaDataThird15,
            bind.criteriaDataThird16)

        Collections.addAll(txtResult3, bind.criteriaResultThird1, bind.criteriaResultThird2, bind.criteriaResultThird3,
            bind.criteriaResultThird4)
    }
}