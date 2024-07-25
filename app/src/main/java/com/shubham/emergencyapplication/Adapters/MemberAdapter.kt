package com.shubham.emergencyapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class MemberAdapter(
    private val context: Context
) : ListAdapter<User, MemberAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.family_member_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.nameTextView.text = item.name
        if(!item.image_url.isNullOrEmpty()){
            Glide.with(context).load(item.image_url).into(holder.photoImageView)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val photoImageView: CircleImageView = itemView.findViewById(R.id.photo)
    }

    private class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }
    }
}
