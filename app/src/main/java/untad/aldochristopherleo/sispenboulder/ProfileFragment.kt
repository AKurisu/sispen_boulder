package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import untad.aldochristopherleo.sispenboulder.adapter.ListUserParticipant
import untad.aldochristopherleo.sispenboulder.data.Participant
import untad.aldochristopherleo.sispenboulder.data.User
import untad.aldochristopherleo.sispenboulder.databinding.FragmentProfileBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var itemList : ArrayList<Participant>
    private lateinit var itemKey : ArrayList<String>
    private lateinit var recyclerView: RecyclerView
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        recyclerView = binding.profileRecycler

        itemList = ArrayList()
        itemKey = ArrayList()

        binding.btnAddParticipant.setOnClickListener {
            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.profileName.text = user.name
            binding.profileType.text = user.type

            if (user.type == "Manajer" || user.type == "Presiden Juri" ) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                viewModel.getAllParticipants().orderByChild("group")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                binding.profileTxt1.visibility = View.VISIBLE
                                itemList.clear()
                                for (item in snapshot.children) {
                                    item.getValue(Participant::class.java)?.let { itemList.add(it) }
                                    item.key?.let { itemKey.add(it) }
                                }
                                val adapter = ListUserParticipant(itemList, itemKey, context)
                                recyclerView.adapter = adapter
                            } else {
                                val adapter = ListUserParticipant(null, ArrayList(), context)
                                recyclerView.adapter = adapter
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelled: ", error.toString())
                        }

                    })
            }
        }
    }

}