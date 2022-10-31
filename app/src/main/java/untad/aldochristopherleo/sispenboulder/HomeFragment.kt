package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.Query
import untad.aldochristopherleo.sispenboulder.adapter.ListEventsAdapter
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.User
import untad.aldochristopherleo.sispenboulder.databinding.FragmentHomeBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listAdapter : ListEventsAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        if (listAdapter != null){
            listAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (listAdapter != null){
            listAdapter.notifyDataSetChanged()
            listAdapter.stopListening()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater,container, false)

        if (activity != null){
            binding.scrollView2.visibility = View.GONE
//            val events = viewModel.getEvents()
            var user: User
            var query : Query = viewModel.getEvents().orderByChild("date")

            viewModel.user.observe(viewLifecycleOwner) { item ->
                user = item

                if (user.type == "Presiden Juri"){
                    query = viewModel.getEvents().orderByChild("president").equalTo(user.name)
                }

                val newOptions = FirebaseRecyclerOptions.Builder<Event>()
                    .setQuery(query, Event::class.java)
                    .build()

                listAdapter.updateOptions(newOptions)

                setHomeDisplay(user)
            }

            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .build()

            listAdapter = ListEventsAdapter(options)
//            listAdapter.setEvent(events)

            with(binding.mainRv){
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = listAdapter
            }
        }
        return binding.root
    }

    private fun setHomeDisplay(user: User) {

        listAdapter.notifyDataSetChanged()
        binding.mainUsername.text = user.name
        binding.scrollView2.visibility = View.VISIBLE
        binding.homeProgressBar.visibility = View.GONE
    }

}