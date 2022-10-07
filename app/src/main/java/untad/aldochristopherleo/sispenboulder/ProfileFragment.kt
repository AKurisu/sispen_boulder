package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import untad.aldochristopherleo.sispenboulder.adapter.ListUserParticipant
import untad.aldochristopherleo.sispenboulder.data.Participant
import untad.aldochristopherleo.sispenboulder.databinding.FragmentProfileBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val recyclerView = binding.profileRecycler

        val itemList = ArrayList<Participant>()
        val itemKey = ArrayList<String>()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.profileName.text = user.name
            binding.profileType.text = user.type

            if (user.type == "Manajer") {
                binding.profileTxt1.visibility = View.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(context)
                viewModel.getAllParticipants().orderByChild("group").equalTo(user.group)
                    .addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                itemList.clear()
                                for (item in snapshot.children){
                                    item.getValue(Participant::class.java)?.let { itemList.add(it) }
                                    item.key?.let { itemKey.add(it) }
                                }
                                val adapter = ListUserParticipant(itemList, itemKey)
                                recyclerView.adapter = adapter
                            } else {
                                val adapter = ListUserParticipant(null, ArrayList())
                                recyclerView.adapter = adapter
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelled: ", error.toString())
                        }

                    })
            }
        }

        return binding.root
    }

}