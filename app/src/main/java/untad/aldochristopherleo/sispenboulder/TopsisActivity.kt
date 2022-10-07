package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import untad.aldochristopherleo.sispenboulder.adapter.ListTableTopsisAdapter
import untad.aldochristopherleo.sispenboulder.data.DataTopsis
import untad.aldochristopherleo.sispenboulder.data.Result
import untad.aldochristopherleo.sispenboulder.data.SortedResult
import untad.aldochristopherleo.sispenboulder.databinding.ActivityTopsisBinding

class TopsisActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
        const val EXTRA_TOPSIS = "extra_topsis"
    }

    private var _bind : ActivityTopsisBinding? = null
    private val bind get() = _bind!!

    private lateinit var event: ArrayList<SortedResult>
    private lateinit var topsis: DataTopsis
    private lateinit var adapter1 : ListTableTopsisAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivityTopsisBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val transition = ChangeBounds()
        transition.duration = 500

        event = intent.getParcelableArrayListExtra<SortedResult>(EXTRA_EVENT) as ArrayList<SortedResult>
        topsis = intent.getParcelableExtra<DataTopsis>(EXTRA_TOPSIS) as DataTopsis

        val rv = bind.expandCvrFirst
        adapter1 = ListTableTopsisAdapter(event)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter1

        populateRv()

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

        bind.cvrFifthButton.setOnClickListener {
            if (bind.expandCvrFifth.visibility == View.VISIBLE){
                TransitionManager.beginDelayedTransition(bind.cvrFifth, transition)
                bind.expandCvrFifth.visibility = View.GONE
                bind.cvrFifthButton.setIconResource(R.drawable.ic_baseline_expand_more_24)
            } else {
                TransitionManager.beginDelayedTransition(bind.cvrFifth, transition)
                bind.expandCvrFifth.visibility = View.VISIBLE
                bind.cvrFifthButton.setIconResource(R.drawable.ic_baseline_expand_less_24)
            }
        }


    }

    private fun populateRv() {
        if (event.isNotEmpty() && topsis.norms!!.isNotEmpty()){
            val event2 = ArrayList<SortedResult>()
            var listIndex = 0
            event.indices.forEach { index ->
                event2.add(SortedResult(event[index].name, null, Result(topsis.norms!![listIndex],
                    topsis.norms!![listIndex + 1], topsis.norms!![listIndex + 2], topsis.norms!![listIndex + 3])))
                listIndex += 4
            }

            val rvTwo = bind.expandCvrSecond
            adapter1 = ListTableTopsisAdapter(event2)
            rvTwo.layoutManager = LinearLayoutManager(this)
            rvTwo.adapter = adapter1

            val event3 = ArrayList<SortedResult>()
            listIndex = 0
            event.indices.forEach { index ->
                event3.add(SortedResult(event[index].name, null, Result(topsis.priorityNorms!![listIndex],
                    topsis.priorityNorms!![listIndex + 1], topsis.priorityNorms!![listIndex + 2], topsis.priorityNorms!![listIndex + 3])))
                listIndex += 4
            }

            val rvThree = bind.expandCvrThird
            adapter1 = ListTableTopsisAdapter(event3)
            rvThree.layoutManager = LinearLayoutManager(this)
            rvThree.adapter = adapter1

            val event41 = ArrayList<SortedResult>()
            val event42 = ArrayList<SortedResult>()
            event.indices.forEach { index ->
                event41.add(SortedResult(event[index].name, topsis.dPos?.get(index), null))
                event42.add(SortedResult(event[index].name, topsis.dNeg?.get(index), null))
            }

            val rvFourthOne = bind.rvFourthOne
            val rvFourthTwo = bind.rvFourthTwo
            adapter1 = ListTableTopsisAdapter(event41)
            rvFourthOne.layoutManager = LinearLayoutManager(this)
            rvFourthOne.adapter = adapter1

            adapter1 = ListTableTopsisAdapter(event42)
            rvFourthTwo.layoutManager = LinearLayoutManager(this)
            rvFourthTwo.adapter = adapter1

            val event5 = ArrayList<SortedResult>()
            event.indices.forEach { index ->
                event5.add(SortedResult(event[index].name, event[index].preferenceValue, null))
            }

            val rvFifth = bind.expandCvrFifth
            adapter1 = ListTableTopsisAdapter(event5)
            rvFifth.layoutManager = LinearLayoutManager(this)
            rvFifth.adapter = adapter1
        }

    }
}