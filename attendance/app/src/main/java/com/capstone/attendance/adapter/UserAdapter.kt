package com.capstone.attendance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.attendance.R
import com.capstone.attendance.data.remote.User

class UserAdapter(private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.name.text = currentItem.name
        holder.time.text = currentItem.time
        holder.icOk.visibility = View.VISIBLE
//        if (currentItem.time!! > "09:00" && currentItem.time!! < "12:00") {
//            holder.icBad.visibility = View.VISIBLE
//            holder.icOk.visibility = View.GONE
//        } else {
//            holder.icBad.visibility = View.GONE
//            holder.icOk.visibility = View.VISIBLE
//        }
    }

    override fun getItemCount(): Int = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_user_name)
        val time: TextView = itemView.findViewById(R.id.tv_user_time)
        val icOk: ImageView = itemView.findViewById(R.id.iv_ok)
        val icBad: ImageView = itemView.findViewById(R.id.iv_bad)
    }
}
