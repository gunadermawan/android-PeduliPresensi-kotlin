package com.capstone.attendance.utils

import android.app.Activity
import android.graphics.Typeface
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

object FunctionLibrary {
    fun toastWarning(
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
}