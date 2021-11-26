package com.capstone.attendance.ui.update

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.capstone.attendance.databinding.FragmentUpdateBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class UpdateFragment : Fragment() {
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
                updateBinding.etPassword.error = "Password harus di isi!"
                updateBinding.etPassword.requestFocus()
                return@setOnClickListener
            }
            user.let {
                val userCredential = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        updateBinding.layoutPassword.visibility = View.GONE
                        updateBinding.layoutEmail.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        updateBinding.etPassword.error = "password Anda salah!"
                        updateBinding.etPassword.requestFocus()
                    } else {
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            updateBinding.btnUpdate.setOnClickListener { view ->
                val email = updateBinding.etEmail.text.toString().trim()
                if (email.isEmpty()) {
                    updateBinding.etEmail.error = "Email tidak boleh kosong!"
                    updateBinding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    updateBinding.etEmail.error = "Email tidak valid!"
                    updateBinding.etEmail.requestFocus()
                    return@setOnClickListener
                }
                user?.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val actionUpdatedEmail = UpdateFragmentDirections.actionUpdatedEmail()
                            Navigation.findNavController(view).navigate(actionUpdatedEmail)
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