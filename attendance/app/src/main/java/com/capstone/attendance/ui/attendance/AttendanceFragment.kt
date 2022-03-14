package com.capstone.attendance.ui.attendance

import android.app.Activity
import android.app.Dialog
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
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.capstone.attendance.R
import com.capstone.attendance.data.remote.User
import com.capstone.attendance.databinding.FragmentAttendanceBinding
import com.capstone.attendance.utils.*
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.lang.Math.toRadians
import java.util.*
import kotlin.math.*

class AttendanceFragment : Fragment() {
    private var _binding: FragmentAttendanceBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var locationRequest: LocationRequest
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
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
                FunctionLibrary.toast(
                    context as Activity,
                    TOAST_WARNING,
                    PERMISSION_GPS,
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                )
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
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET
            ),
            LOCATION_PERMISSION
        )
    }

    private fun onClick() {
        binding.fabCheckIn.setOnClickListener {
            loadScanLocation()
            Handler(Looper.getMainLooper()).postDelayed({
                getLastLocation()
            }, DELAY_LOCATION)
        }
    }

    private fun getLastLocation() {
        if (FunctionLibrary.checkConnection(requireContext())) {
            if (timeAttendance() || timeAttendanceLate()) {
                if (checkPermission()) {
                    if (isLocationEnabled()) {
                        val locationCallBack = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                val location = locationResult.lastLocation
                                val currentLat = location.latitude
                                val currentLong = location.longitude
                                val destinationLat = getAddress()[0].latitude
                                val destinationLong = getAddress()[0].longitude
                                val distance = calculateDistance(
                                    currentLat, currentLong, destinationLat, destinationLong
                                ) * 1000
                                Log.d(TAG, "$TAG_RESULT - $distance")
                                if (distance < MEASURING_DISTANCE) {
                                    showDialogForm()
                                    FunctionLibrary.toast(
                                        context as Activity,
                                        TOAST_SUCCESS,
                                        LOCATION_FOUND,
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(
                                            context as Activity,
                                            R.font.helveticabold
                                        )
                                    )
                                } else {
                                    FunctionLibrary.toast(
                                        context as Activity,
                                        TOAST_WARNING,
                                        OUT_OF_RANGE,
                                        MotionToastStyle.WARNING,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(
                                            context as Activity,
                                            R.font.helveticabold
                                        )
                                    )
                                    binding.tvCheckIn.visibility = View.VISIBLE
                                }
                                fusedLocationProviderClient?.removeLocationUpdates(this)
                                stopScanLocation()
                            }
                        }
                        fusedLocationProviderClient?.requestLocationUpdates(
                            locationRequest,
                            locationCallBack,
                            Looper.getMainLooper()
                        )
                    } else {
                        FunctionLibrary.toast(
                            context as Activity,
                            TOAST_WARNING,
                            PERMISSION_GPS,
                            MotionToastStyle.WARNING,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                        )
                        stopScanLocation()
                    }
                } else {
                    stopScanLocation()
                    requestPermission()
                }
            }
        } else {
            FunctionLibrary.toast(
                context as Activity,
                TOAST_ERROR,
                PERMISSION_INTERNET,
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
            )
            stopScanLocation()
        }
    }

    private fun timeAttendance(): Boolean {
        return FunctionLibrary.getCurrentTime()!! > "07:00" && FunctionLibrary.getCurrentTime()!! < "09:00"
    }

    private fun timeAttendanceLate(): Boolean {
        return FunctionLibrary.getCurrentTime()!! > "09:00" && FunctionLibrary.getCurrentTime()!! < "12:00"
    }

    private fun showDialogForm() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_dialog_form)
        val btnYes = dialog.findViewById<Button>(R.id.btn_save)
        btnYes.setOnClickListener {
            val user = auth.currentUser
            val name = if (user?.displayName == null) {
                dialog.findViewById<EditText>(R.id.et_name_attendance).text.toString().trim()
            } else {
                user.displayName
            }
            if (name != null) {
                if (name.isNotEmpty()) {
                    inputToFirebase(name)
                } else {
                    FunctionLibrary.toast(
                        context as Activity,
                        TOAST_ERROR,
                        INPUT_YOUR_NAME,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                    )
                }
            }
            dialog.dismiss()
        }
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun inputToFirebase(name: String) {
        val database = FirebaseDatabase.getInstance()
        val attendanceRef = database.getReference(REALTIME_DB)
        val userId = attendanceRef.push().key
        val user = User(userId, name, FunctionLibrary.getCurrentTime())
        attendanceRef.child(name).setValue(user)
            .addOnCompleteListener {
                FunctionLibrary.toast(
                    context as Activity,
                    TOAST_SUCCESS,
                    ATTENDANCE_SUCCESSFUL,
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                )
            }
            .addOnFailureListener {
                FunctionLibrary.toast(
                    context as Activity,
                    TOAST_WARNING,
                    "${it.message}",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                )
            }
    }

    private fun getAddress(): List<Address> {
        val destinationPlace = ADDRESS_GEOCODER
        val geocode = Geocoder(context, Locale.getDefault())
        return geocode.getFromLocationName(destinationPlace, MAX_RESULT)
    }

    private fun loadScanLocation() {
        binding.rippleBackground.startRippleAnimation()
        binding.tvScanning.visibility = View.VISIBLE
        binding.tvCheckIn.visibility = View.GONE
        binding.tvCheckInSuccess.visibility = View.GONE
        binding.fabCheckIn.visibility = View.GONE
        binding.gpsAnimation.visibility = View.VISIBLE
    }

    private fun stopScanLocation() {
        binding.rippleBackground.stopRippleAnimation()
        binding.tvScanning.visibility = View.GONE
        binding.tvCheckIn.visibility = View.VISIBLE
        binding.gpsAnimation.visibility = View.GONE
        binding.fabCheckIn.visibility = View.VISIBLE
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
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(radianLat1) * cos(
                    radianLat2
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}