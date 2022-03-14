package com.capstone.attendance.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

object FunctionLibrary {
    fun toast(
        context: Activity,
        title: String?,
        message: String,
        style: MotionToastStyle,
        position: Int,
        duration: Long,
        font: Typeface?
    ) {
        MotionToast.createColorToast(context, title, message, style, position, duration, font)
    }

    fun checkConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun getCurrentTime(): String? {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, long2: Double): Double {
        val r = 6372.8
        val radianLat1 = Math.toRadians(lat1)
        val radianLat2 = Math.toRadians(lat2)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(long2 - lon1)
        return 2 * r * asin(
            sqrt(
                sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(radianLat1) * cos(
                    radianLat2
                )
            )
        )
    }

    fun timeAttendance(): Boolean {
        return FunctionLibrary.getCurrentTime()!! > "07:00" && FunctionLibrary.getCurrentTime()!! < "09:00"
    }

    fun timeAttendanceLate(): Boolean {
        return FunctionLibrary.getCurrentTime()!! > "09:00" && FunctionLibrary.getCurrentTime()!! < "12:00"
    }
}