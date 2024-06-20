package com.bucket.list.bucketlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.CompleteItemBinding
import com.bucket.list.bucketlist.models.CompleteGoalModel
import com.bucket.list.bucketlist.models.OnItemClickListener

class CompletGoalAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<CompletGoalAdapter.CompleteHolder>() {

    var completeGoalList = mutableListOf<CompleteGoalModel>()

    inner class CompleteHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = CompleteItemBinding.bind(item)
        fun bind(complete: CompleteGoalModel) {
            binding.title.text = complete.title
            binding.login.text = complete.login
            binding.next.setOnClickListener {
                listener.OnClickListener(complete)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompleteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.complete_item, parent, false)
        return CompleteHolder(view)
    }

    override fun getItemCount(): Int {
        return completeGoalList.size
    }

    override fun onBindViewHolder(holder: CompleteHolder, position: Int) {
        val complete = completeGoalList[position]
        holder.bind(complete)
    }
}