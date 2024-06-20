package com.bucket.list.bucketlist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bucket.list.bucketlist.R
import com.bucket.list.bucketlist.databinding.AddItemBinding
import com.bucket.list.bucketlist.databinding.GoalHomeItemBinding
import com.bucket.list.bucketlist.models.AddModel
import com.bucket.list.bucketlist.models.GoalHomeModel

class AddAdapter(private val onAddClickListener: (AddModel) -> Unit):  RecyclerView.Adapter<AddAdapter.AddHolder>() {

    var addList = mutableListOf<AddModel>()

    class AddHolder(item: View, private val onAddClickListener: (AddModel) -> Unit) : RecyclerView.ViewHolder(item) {
        val binding = AddItemBinding.bind(item)
        fun bind(add : AddModel){
            binding.title.text = add.title
            binding.login.text = add.login
            binding.add.setOnClickListener {
                onAddClickListener(add)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_item, parent, false)
        return AddHolder(view, onAddClickListener)
    }

    override fun getItemCount(): Int {
        return addList.size
    }

    override fun onBindViewHolder(holder: AddHolder, position: Int) {
        val add = addList[position]
        holder.bind(add)
    }
}