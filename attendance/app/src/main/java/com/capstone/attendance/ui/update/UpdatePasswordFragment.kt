package com.capstone.attendance.ui.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.capstone.attendance.databinding.FragmentUpdatePasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdatePasswordFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var updatePasswordBinding: FragmentUpdatePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        updatePasswordBinding =
            FragmentUpdatePasswordBinding.inflate(layoutInflater, container, false)
        return updatePasswordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        updatePasswordBinding.layoutPassword.visibility = View.VISIBLE
        updatePasswordBinding.layoutNewPass.visibility = View.GONE
        updatePasswordBinding.btnAuth.setOnClickListener {
            val pass = updatePasswordBinding.etPassword.text.toString().trim()
            if (pass.isEmpty()) {
                updatePasswordBinding.txtInputPassword.error = "Password harus di isi!"
                updatePasswordBinding.txtInputPassword.requestFocus()
                return@setOnClickListener
            }
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener {Task ->
                    when {
                        Task.isSuccessful -> {
                            updatePasswordBinding.layoutPassword.visibility = View.GONE
                            updatePasswordBinding.layoutNewPass.visibility = View.VISIBLE
                        }
                        Task.exception is FirebaseAuthInvalidCredentialsException -> {
                            updatePasswordBinding.txtInputPassword.error = "password Anda salah!"
                            updatePasswordBinding.txtInputPassword.requestFocus()
                        }
                        else -> {
                            Toast.makeText(activity, "${Task.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            updatePasswordBinding.btnUpdate.setOnClickListener btnUpdate@ { view ->
                val newPass = updatePasswordBinding.etNewPassword.text.toString().trim()
                val newPassConfirm = updatePasswordBinding.etNewPasswordConfirm.text.toString().trim()
                if (newPass.isEmpty() || newPass.length < 8) {
                    updatePasswordBinding.txtInputNewPassword.error = "Password tidak harus diisi!"
                    updatePasswordBinding.txtInputNewPassword.requestFocus()
                    return@btnUpdate
                }
                if (newPass != newPassConfirm) {
                    updatePasswordBinding.txtInputNewPasswordConfirm.error = "password tidak sama!"
                    updatePasswordBinding.txtInputNewPasswordConfirm.requestFocus()
                    return@btnUpdate
                }

                user?.let {
                    user.updatePassword(newPass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val actionUpdatedPass =
                               UpdatePasswordFragmentDirections.actionUpdatedPassword()
                            Navigation.findNavController(view).navigate(actionUpdatedPass)
                            Toast.makeText(activity, "Password berhasil dirubah.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}