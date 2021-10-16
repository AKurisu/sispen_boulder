package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
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
//            val events = viewModel.getEvents()

            binding.scrollView2.visibility = View.GONE
            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(viewModel.getEvents().orderByChild("date"), Event::class.java)
                .build()


            listAdapter = ListEventsAdapter(options)
//            listAdapter.setEvent(events)

            with(binding.mainRv){
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = listAdapter
            }

            viewModel.user.observe(viewLifecycleOwner) { user ->
                binding.mainUsername.text = user.name
                setHomeDisplay(user)
            }
        }
        return binding.root
    }

    private fun setHomeDisplay(user: User) {
        binding.mainUsername.text = user.name
        if (user.type == "Juri"){
            binding.cardView.visibility = View.GONE

        }
        binding.scrollView2.visibility = View.VISIBLE
        binding.homeProgressBar.visibility = View.GONE
    }

}