package com.capstone.attendance.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String? = null,
    var name: String? = null,
    var time: String? = null
) : Parcelable