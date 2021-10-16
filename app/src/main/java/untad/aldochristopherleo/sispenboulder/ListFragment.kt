package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import untad.aldochristopherleo.sispenboulder.adapter.ListEventDetailAdapter
import untad.aldochristopherleo.sispenboulder.adapter.ListEventsAdapter
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.databinding.FragmentHomeBinding
import untad.aldochristopherleo.sispenboulder.databinding.FragmentListBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class ListFragment : Fragment() {


    private lateinit var adapterDone : ListEventDetailAdapter
    private lateinit var adapterUpcoming : ListEventDetailAdapter
    private lateinit var binding: FragmentListBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater,container, false)

        if (activity != null){

            val listDone = ArrayList<Event>()
            val listUpcoming = ArrayList<Event>()

            val rvDone = binding.listRvDone
            val rvUpcoming = binding.listRvUpcoming
            adapterDone = ListEventDetailAdapter(listDone)
            adapterUpcoming = ListEventDetailAdapter(listUpcoming)

            viewModel.getEvents().addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listDone.clear()
                    listUpcoming.clear()
                    snapshot.children.forEach { item ->
                        if (item.child("finished").value == true){
                            listDone.add(item.getValue(Event::class.java)!!)
                        } else listUpcoming.add(item.getValue(Event::class.java)!!)
                    }
                    adapterDone.notifyDataSetChanged()
                    adapterUpcoming.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            rvDone.layoutManager = LinearLayoutManager(context)
            rvDone.adapter = adapterDone

            rvUpcoming.layoutManager = LinearLayoutManager(context)
            rvUpcoming.adapter = adapterUpcoming

            binding.btnListDone.setOnClickListener{
                if (rvDone.visibility == View.VISIBLE){
                    val transition = ChangeBounds()
                    transition.duration = 500
                    TransitionManager.beginDelayedTransition(rvDone, transition)
                    rvDone.visibility = View.GONE
                    binding.btnListDone.setIconResource(R.drawable.ic_baseline_arrow_drop_down_24)
                } else {
                    val transition = ChangeBounds()
                    transition.duration = 500
                    TransitionManager.beginDelayedTransition(rvDone, transition)
                    rvDone.visibility = View.VISIBLE
                    binding.btnListDone.setIconResource(R.drawable.ic_baseline_arrow_drop_up_24)
                }
            }

            binding.btnListUpcoming.setOnClickListener{
                if (rvUpcoming.visibility == View.VISIBLE){
                    val transition = ChangeBounds()
                    transition.duration = 500
                    TransitionManager.beginDelayedTransition(rvUpcoming, transition)
                    rvUpcoming.visibility = View.GONE
                    binding.btnListDone.setIconResource(R.drawable.ic_baseline_arrow_drop_down_24)
                } else {
                    val transition = ChangeBounds()
                    transition.duration = 500
                    TransitionManager.beginDelayedTransition(rvUpcoming, transition)
                    rvUpcoming.visibility = View.VISIBLE
                    binding.btnListDone.setIconResource(R.drawable.ic_baseline_arrow_drop_up_24)
                }
            }
        }

        return binding.root
    }
}