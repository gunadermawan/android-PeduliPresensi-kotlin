package com.capstone.attendance.ui.home

import android.content.Intent
import android.net.Uri
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
                binding.pbHomeProfile.visibility = View.GONE
            } else {
                Picasso.get().load(PATH_DEFAULT_PROFILE)
                    .into(binding.ivUserProfileHome)
                binding.pbHomeProfile.visibility = View.GONE
            }
            if (user.displayName == null) {
                binding.tvUserHome.text = getString(R.string.user)
            } else {
                binding.tvUserHome.text = user.displayName
            }
        }
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 3..11 -> {
                binding.tvWelcome.text = GOOD_MORNING
                binding.ivMorning.visibility = View.VISIBLE
            }
            in 12..15 -> {
                binding.tvWelcome.text = GOOD_AFTERNOON
                binding.ivAfternoon.visibility = View.VISIBLE
            }
            in 16..18 -> {
                binding.tvWelcome.text = GOOD_EVENING
                binding.ivEvening.visibility = View.VISIBLE
            }
            in 19..23 -> {
                binding.tvWelcome.text = GOOD_NIGHT
                binding.ivNight.visibility = View.VISIBLE
            }
            else -> {
                binding.tvWelcome.text = GOOD_LATE_NIGHT
                binding.ivNight.visibility = View.VISIBLE
            }
        }
        binding.iv1.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.laporgub.jatengprov.go.id/")).also {
                startActivity(it)
            }
        }
        binding.iv2.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=id.lapor.bupati3")
            ).also {
                startActivity(it)
            }
        }
        binding.iv3.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://covid19.tegalkab.go.id/")).also {
                startActivity(it)
            }
        }
        binding.iv4.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://ppid.tegalkab.go.id/")).also {
                startActivity(it)
            }
        }
        binding.iv5.setOnClickListener {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://utama.tegalkab.go.id/")).also {
                startActivity(it)
            }
        }
        binding.iv6.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://disdukcapil.tegalkab.go.id/")
            ).also {
                startActivity(it)
            }
        }
        binding.cvNews1.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.jawapos.com/jpg-today/01/12/2021/buat-aplikasi-lapor-gub-cara-ganjar-respons-aduan-pungli-di-jateng/?msclkid=288292e4a9ef11ec8e1ab4d36f1a3a76")
            ).also {
                startActivity(it)
            }
        }
        binding.cvNews2.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://regional.kompas.com/read/2021/06/29/195730778/polres-tegal-kota-gelar-vaksinasi-covid-19-gratis-tanpa-surat-domisili?msclkid=50e6f242a9ef11eca876bccc7a4397d9")
            ).also {
                startActivity(it)
            }
        }
        binding.cvNews3.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://jatengprov.go.id/beritadaerah/layanan-aduan-lapor-bupati-tegal-hadir-di-versi-android/?msclkid=dc53f2f0a9f011ec8f5a399a0a2d84f9")
            ).also {
                startActivity(it)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onClick(v: View?) {
//        when (view) {
//            binding.iv1 -> {
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://www.laporgub.jatengprov.go.id/")
//                ).also {
//                    startActivity(it)
//                }
//            }
//        }
//    }
}

