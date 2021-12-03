package com.capstone.attendance.ui.attendance

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.capstone.attendance.databinding.FragmentAttendanceBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import java.lang.Math.toRadians
import java.util.*
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AttendanceFragment : Fragment() {

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

        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
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
            }, 2000)
        }
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                val locationCallBack = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        val location = locationResult.lastLocation
                        val currentLat = location.latitude
                        val currentLong = location.longitude
//                        geocoder
                        val destinationLat = getAddress()[0].latitude
                        val destinationLong = getAddress()[0].longitude
                        val distance = calculateDistance(
                            currentLat, currentLong, destinationLat, destinationLong
                        ) * 1000
                        Log.d("MainActivity","[onLocationResult] - $distance")
                        if (distance<10.0){
                            showDialogForm()
                            Toast.makeText(activity, "Lokasi ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun showDialogForm() {

    }

    private fun getAddress(): List<Address> {
        val destinationPlace = "Balai Desa Warureja"
        val geocode = Geocoder(context, Locale.getDefault())
        return geocode.getFromLocationName(destinationPlace, 100)
    }

    private fun loadScanLocation() {
        binding.rippleBackground.startRippleAnimation()
        binding.tvScanning.visibility = View.VISIBLE
        binding.tvCheckIn.visibility = View.GONE
        binding.tvCheckInSuccess.visibility = View.GONE
    }

    private fun stopScanLocation() {
        binding.rippleBackground.stopRippleAnimation()
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

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, long2: Double): Double {
        val r = 6372.8
        val radianLat1 = toRadians(lat1)
        val radianLat2 = toRadians(lat2)
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(long2 - lon1)
        return 2 * r * asin(
            sqrt(
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * kotlin.math.cos(radianLat1) * kotlin.math.cos(
                    radianLat2
                )
            )
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOCATION_PERMISSION = 0
    }
}