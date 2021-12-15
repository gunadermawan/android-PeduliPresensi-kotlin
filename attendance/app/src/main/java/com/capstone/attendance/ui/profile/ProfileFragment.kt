package com.capstone.attendance.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.capstone.attendance.R
import com.capstone.attendance.databinding.FragmentProfileBinding
import com.capstone.attendance.ui.login.LoginActivity
import com.capstone.attendance.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var imgUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(profileBinding.ivProfile)
            } else {
                Picasso.get().load(PATH_DEFAULT_PROFILE)
                    .into(profileBinding.ivProfile)
            }
            profileBinding.progressBar2.visibility = View.GONE
            profileBinding.etName.setText(user.displayName)
            profileBinding.etEmail.setText(user.email)
            if (user.isEmailVerified) {
                profileBinding.ivVerified.visibility = View.VISIBLE
                profileBinding.tvVerified.visibility = View.VISIBLE
            } else {
                profileBinding.ivUnverified.visibility = View.VISIBLE
                profileBinding.tvUnverified.visibility = View.VISIBLE
            }
        }
        profileBinding.btnUpdate.setOnClickListener {
            val image = when {
                ::imgUri.isInitialized -> imgUri
                user?.photoUrl == null -> Uri.parse(PATH_DEFAULT_PROFILE)
                else -> user.photoUrl
            }
            val name = profileBinding.etName.text.toString().trim()
            if (name.isEmpty()) {
                profileBinding.textInputName.error = NAME_EMPTY
                profileBinding.textInputName.requestFocus()
                return@setOnClickListener
            }
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener { Task ->
                        if (Task.isSuccessful) {
                            Toast.makeText(
                                activity,
                                PROFILE_UPDATED,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else
                            FunctionLibrary.toast(
                                context as Activity,
                                TOAST_WARNING,
                                "${Task.exception?.message}",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                            )
                    }
                }
        }
        profileBinding.btnLogout.setOnClickListener {
            auth.signOut()
            Intent(activity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
        profileBinding.ivProfile.setOnClickListener {
            intentCamera()
        }
        profileBinding.btnVerification.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    FunctionLibrary.toast(
                        context as Activity,
                        TOAST_SUCCESS,
                        EMAIL_VERIFICATION,
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                    )
                } else {
                    FunctionLibrary.toast(
                        context as Activity,
                        TOAST_ERROR,
                        EMAIL_VERIFICATION_ERROR,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context as Activity, R.font.helveticabold)
                    )
                }
            }
        }
        profileBinding.btnChangeEmail.setOnClickListener {
            val updateEmail = ProfileFragmentDirections.actionUpdateEmail()
            Navigation.findNavController(it).navigate(updateEmail)
        }
        profileBinding.btnChangePassword.setOnClickListener {
            val updatePass = ProfileFragmentDirections.actionUpdatePassword()
            Navigation.findNavController(it).navigate(updatePass)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImgBitmap(imgBitmap)
        }
    }

    private fun uploadImgBitmap(imgBitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance().reference.child("img_profile/${FirebaseAuth.getInstance().currentUser?.uid}")
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val img = byteArrayOutputStream.toByteArray()
        ref.putBytes(img)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result?.let { Uri ->
                            imgUri = Uri
                            profileBinding.ivProfile.setImageBitmap(imgBitmap)
                        }
                    }
                }
            }
    }
}