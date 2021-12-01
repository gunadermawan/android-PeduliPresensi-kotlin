package com.capstone.attendance.ui.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.capstone.attendance.R
import com.capstone.attendance.databinding.FragmentUpdateBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdateEmailFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var updateBinding: FragmentUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        updateBinding = FragmentUpdateBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return updateBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        updateBinding.layoutPassword.visibility = View.VISIBLE
        updateBinding.layoutEmail.visibility = View.GONE
        updateBinding.btnAuth.setOnClickListener {
            val pass = updateBinding.etPassword.text.toString().trim()
            if (pass.isEmpty()) {
                updateBinding.txtInputPassword.error = "Password harus di isi!"
                updateBinding.txtInputPassword.requestFocus()
                return@setOnClickListener
            }
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener {Task ->
                    when {
                        Task.isSuccessful -> {
                            updateBinding.layoutPassword.visibility = View.GONE
                            updateBinding.layoutEmail.visibility = View.VISIBLE
                        }
                        Task.exception is FirebaseAuthInvalidCredentialsException -> {
                            updateBinding.txtInputPassword.error = "password Anda salah!"
                            updateBinding.txtInputPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(activity, "${Task.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            updateBinding.btnUpdate.setOnClickListener  BtnUpdate@ { view ->
                val email = updateBinding.etEmail.text.toString().trim()
                if (email.isEmpty()) {
                    updateBinding.txtInputEmail.error = "Email tidak boleh kosong!"
                    updateBinding.txtInputEmail.requestFocus()
                    return@BtnUpdate
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    updateBinding.txtInputEmail.error = "Email tidak valid!"
                    updateBinding.txtInputEmail.requestFocus()
                    return@BtnUpdate
                }
                user?.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val actionUpdatedEmail =
                                UpdateEmailFragmentDirections.actionUpdatedEmail()
                            Navigation.findNavController(view).navigate(actionUpdatedEmail)
                            val mNotificationManager =
                                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val mBuilder = NotificationCompat.Builder(view.context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_finger)
                                .setLargeIcon(
                                    BitmapFactory.decodeResource(
                                        resources,
                                        R.drawable.ic_finger
                                    )
                                )
                                .setContentTitle(resources.getString(R.string.update_profile_succes))
                                .setContentText(resources.getString(R.string.update_email_succes_desc))
                                .setAutoCancel(true)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val channel = NotificationChannel(
                                    CHANNEL_ID,
                                    CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                                channel.description = CHANNEL_NAME
                                mBuilder.setChannelId(CHANNEL_ID)
                                mNotificationManager.createNotificationChannel(channel)
                            }
                            val notification = mBuilder.build()
                            mNotificationManager.notify(NOTIFICATION_ID, notification)
                            Toast.makeText(activity, "Email berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}