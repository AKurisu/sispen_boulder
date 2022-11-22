package untad.aldochristopherleo.sispenboulder

import android.net.Uri
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import untad.aldochristopherleo.sispenboulder.adapter.ListEventsAdapter
import untad.aldochristopherleo.sispenboulder.data.Event
import untad.aldochristopherleo.sispenboulder.data.User
import untad.aldochristopherleo.sispenboulder.databinding.FragmentHomeBinding
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater,container, false)

        if (activity != null){
            binding.scrollView2.visibility = View.GONE
//            val events = viewModel.getEvents()
            var user: User

            viewModel.user.observe(viewLifecycleOwner) { item ->
                setHomeDisplay(item)
            }
        }
        return binding.root
    }

    private fun setHomeDisplay(user: User) {
        binding.mainUsername.text = user.name
        binding.mainUserType.text = user.type
        binding.scrollView2.visibility = View.VISIBLE
        binding.homeProgressBar.visibility = View.GONE

        val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (acct != null) {
            val personPhoto: Uri? = acct.photoUrl // this line to get  profile picture
            Picasso.get().load(personPhoto).into(binding.imgProfil)
        }
    }

}