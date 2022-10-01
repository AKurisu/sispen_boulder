package untad.aldochristopherleo.sispenboulder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.profileName.text = user.name
            binding.profileType.text = user.type

            if (user.type == "Manajer") {

            }
        }

        return binding.root
    }

}