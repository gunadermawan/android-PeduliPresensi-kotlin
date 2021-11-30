package com.capstone.attendance.ui.attendance


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.capstone.attendance.databinding.FragmentAttendanceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class AttendanceFragment : Fragment() {

    private lateinit var attendanceViewModel: AttendanceViewModel
    private var _binding: FragmentAttendanceBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        attendanceViewModel =
            ViewModelProvider(this).get(AttendanceViewModel::class.java)

        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        initLocation()
        checkPermissionLocations()
        onClick()
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkPermissionLocations() {
        if (checkPermission()) {
            if (!isLocationEnabled()) {
                Toast.makeText(
                    activity,
                    "Aktifkan GPS Anda untuk memulai presensi",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } else {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireContext() as Activity, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION
        )
    }

    private fun onClick() {
        binding.fabCheckIn.setOnClickListener {
            loadScanLocation()
            Handler(Looper.getMainLooper()).postDelayed({
                getLastLocation()
            },2000)
        }
    }

    private fun getLastLocation() {

    }

    private fun loadScanLocation() {
        binding.rippleBackground.startRippleAnimation()
        binding.tvScanning.visibility = View.GONE
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) {
            return true
        }
        return false
    }

    private fun initLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.create().apply {
            interval = 1000 * 5
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOCATION_PERMISSION = 0
    }
}