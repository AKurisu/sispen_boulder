package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import untad.aldochristopherleo.sispenboulder.adapter.ListEventDetailAdapter
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.databinding.FragmentListBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var rvDone : RecyclerView
    private lateinit var rvUpcoming : RecyclerView
    private val viewModel: MainViewModel by viewModels()
    private var userName = ""
    private var userType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater,container, false)

        if (activity != null){

            viewModel.user.observe(viewLifecycleOwner){ user ->
                userName = user.name.toString()
                userType = user.type.toString()

                rvDone = binding.listRvDone
                rvUpcoming = binding.listRvUpcoming

                setRecyclerView()
                setOnClickListener()
            }

        }

        return binding.root
    }

    private fun setOnClickListener() {
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

        if (userType == "Juri Lapangan"){
            binding.btnAddEvent.visibility = View.GONE
        } else {
            binding.btnAddEvent.setOnClickListener {
                val intent = Intent(context, AddEventActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setRecyclerView() {
        val listDone = ArrayList<Event>()
        val listUpcoming = ArrayList<Event>()
        val eventKeysDone = ArrayList<String>()
        val eventKeysUpcoming = ArrayList<String>()
        val adapterDone = ListEventDetailAdapter(listDone, eventKeysDone, userName, userType)
        val adapterUpcoming = ListEventDetailAdapter(listUpcoming, eventKeysUpcoming, userName, userType)

        viewModel.getEvents().orderByChild("date").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listDone.clear()
                listUpcoming.clear()
                snapshot.children.forEach { item ->
                    if (item.child("finished").value == true){
                        if (userType == "Presiden Juri"){
                            if (item.child("president").value != userName){
                                return@forEach
                            }
                        }
                        eventKeysDone.add(item.key.toString())
                        listDone.add(item.getValue(Event::class.java)!!)
                    } else {
                        if (userType == "Presiden Juri"){
                            if (item.child("president").value != userName){
                                return@forEach
                            }
                        }
                        eventKeysUpcoming.add(item.key.toString())
                        listUpcoming.add(item.getValue(Event::class.java)!!)
                    }
                }
                adapterDone.notifyDataSetChanged()
                adapterUpcoming.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        with(rvDone){
            layoutManager = LinearLayoutManager(context)
            adapter = adapterDone
        }

        with(rvUpcoming){
            layoutManager = LinearLayoutManager(context)
            adapter = adapterUpcoming
        }
    }
}