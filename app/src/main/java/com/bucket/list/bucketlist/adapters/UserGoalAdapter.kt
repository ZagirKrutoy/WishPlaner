package com.bucket.list.bucketlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.UserItemBinding
import com.bucket.list.bucketlist.models.AddModel
import com.bucket.list.bucketlist.models.UserGoalModel

class UserGoalAdapter(private val onAddClickListener: (UserGoalModel) -> Unit): RecyclerView.Adapter<UserGoalAdapter.UserHolder>() {

    var userGoalList = mutableListOf<UserGoalModel>()

    class UserHolder(item : View, private val onAddClickListener: (UserGoalModel) -> Unit): RecyclerView.ViewHolder(item) {
        val binding = UserItemBinding.bind(item)
        fun bind(user : UserGoalModel){
            binding.title.text = user.title
            binding.login.text = user.login
            binding.next.setOnClickListener {
                onAddClickListener(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserHolder(view, onAddClickListener)
    }

    override fun getItemCount(): Int {
        return userGoalList.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user = userGoalList[position]
        holder.bind(user)
    }
}