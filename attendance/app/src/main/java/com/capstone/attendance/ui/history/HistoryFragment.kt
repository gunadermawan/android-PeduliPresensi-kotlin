package com.capstone.attendance.ui.history

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.attendance.adapter.UserAdapter
import com.capstone.attendance.data.remote.User
import com.capstone.attendance.databinding.FragmentHistoryBinding
import com.capstone.attendance.utils.REALTIME_DB
import com.capstone.attendance.utils.REALTIME_DB_CANCELED
import com.capstone.attendance.viewModel.HistoryViewModel
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var progressBar: ProgressBar
    private lateinit var txtLoadingProgressBar: TextView

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRecyclerView = binding.userList
        userRecyclerView.layoutManager = LinearLayoutManager(context)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        progressBar = binding.progressBar
        txtLoadingProgressBar = binding.tvProgressbar
        getUserData()
    }

    private fun getUserData() {
        dbRef = FirebaseDatabase.getInstance().getReference(REALTIME_DB)
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    progressBar.visibility = View.GONE
                    txtLoadingProgressBar.visibility = View.GONE
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userArrayList.add(user)
                    }
                }
                userRecyclerView.adapter = UserAdapter(userArrayList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, REALTIME_DB_CANCELED, error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}