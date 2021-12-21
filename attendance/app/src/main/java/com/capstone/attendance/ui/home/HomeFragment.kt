package com.capstone.attendance.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.attendance.R
import com.capstone.attendance.databinding.FragmentHomeBinding
import com.capstone.attendance.utils.*
import com.capstone.attendance.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.util.*

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
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> binding.tvWelcome.text = GOOD_MORNING
            in 12..15 -> binding.tvWelcome.text = GOOD_AFTERNOON
            in 16..18 -> binding.tvWelcome.text = GOOD_EVENING
            in 17..23 -> binding.tvWelcome.text = GOOD_NIGHT
            else -> binding.tvUserHome.text = GOOD_LATE_NIGHT
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}