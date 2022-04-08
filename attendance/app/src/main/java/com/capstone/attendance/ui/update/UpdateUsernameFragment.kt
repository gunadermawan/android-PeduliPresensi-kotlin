package com.capstone.attendance.ui.update

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.capstone.attendance.R
import com.capstone.attendance.databinding.FragmentUpdateUsernameBinding
import com.capstone.attendance.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class UpdateUsernameFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var updateUsernameBinding: FragmentUpdateUsernameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        updateUsernameBinding =
            FragmentUpdateUsernameBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return updateUsernameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        updateUsernameBinding.etUsername.requestFocus()
        val user = auth.currentUser
        updateUsernameBinding.etUsername.setText(user?.displayName)
        updateUsernameBinding.btnUpdate.setOnClickListener {
            val name = updateUsernameBinding.etUsername.text.toString()
            if (name.isEmpty()) {
                updateUsernameBinding.txtInputUsername.error = NAME_EMPTY
                updateUsernameBinding.txtInputUsername.requestFocus()
                return@setOnClickListener
            }
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener { Task ->
                        if (Task.isSuccessful) {
                            val actionUpdateUsername =
                                UpdateUsernameFragmentDirections.actionUpdatedUsername()
                            Navigation.findNavController(view).navigate(actionUpdateUsername)
                            FunctionLibrary.toast(
                                context as Activity,
                                TOAST_SUCCESS,
                                PROFILE_UPDATED,
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context as Activity,
                                    R.font.helveticabold)
                            )
                        } else {
                            FunctionLibrary.toast(
                                context as Activity,
                                TOAST_WARNING,
                                "${Task.exception?.message}",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context as Activity,
                                    R.font.helveticabold)
                            )
                        }
                    }
                }
        }
    }
}