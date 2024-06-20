package com.bucket.list.bucketlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.GoalHomeItemBinding
import com.bucket.list.bucketlist.models.GoalHomeModel
import com.bumptech.glide.Glide

class GoalHomeAdapter: RecyclerView.Adapter<GoalHomeAdapter.GoalHomeHolder>() {

    var goalHomeList = mutableListOf<GoalHomeModel>()

    class GoalHomeHolder(item : View): RecyclerView.ViewHolder(item) {
        val binding = GoalHomeItemBinding.bind(item)
        fun bind(goal : GoalHomeModel){
            binding.name.text = goal.name
            binding.title.text = goal.title
            binding.login.text = goal.login
            Glide.with(itemView)
                .load(goal.image)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalHomeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.goal_home_item, parent, false)
        return GoalHomeHolder(view)
    }

    override fun getItemCount(): Int {
        return goalHomeList.size
    }

    override fun onBindViewHolder(holder: GoalHomeHolder, position: Int) {
        val goal = goalHomeList[position]
        holder.bind(goal)
    }
}