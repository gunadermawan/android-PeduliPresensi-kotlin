package com.capstone.attendance.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.attendance.R
import com.capstone.attendance.databinding.FragmentHomeBinding
import com.capstone.attendance.utils.PATH_DEFAULT_PROFILE
import com.capstone.attendance.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivUserProfileHome)
            } else {
                Picasso.get().load(PATH_DEFAULT_PROFILE)
                    .into(binding.ivUserProfileHome)
            }
            if (user.displayName == null) {
                binding.tvUserHome.text = getString(R.string.user)
            } else {
                binding.tvUserHome.text = user.displayName
            }
        }
        binding.btnGps.setOnClickListener {
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also {
                startActivity(it)
            }
        }
        binding.btnConnections.setOnClickListener {
            Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}