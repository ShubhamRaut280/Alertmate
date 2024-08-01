package com.shubham.emergencyapplication.Adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shubham.emergencyapplication.BottomSheets.DialogUtils.showUserDetailsSheet
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import de.hdodenhof.circleimageview.CircleImageView

class MemberAdapter(
    private val context: Context,
    private val onItemClick: (User) -> Unit // Click listener
) : ListAdapter<User, MemberAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.family_member_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.nameTextView.text = item.name
        holder.nameTextView.isSelected = true

        if (!item.image_url.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.image_url)
                .placeholder(R.drawable.load)
                .into(holder.photoImageView)
        }

        holder.photoImageView.borderColor = if (item.emergency) {
            context.resources.getColor(R.color.red)
        } else {
            context.resources.getColor(android.R.color.transparent) // Reset border color if not in emergency
        }
        holder.inEmergency.visibility = if (item.emergency) View.VISIBLE else View.GONE

        // Set item click listener
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val photoImageView: CircleImageView = itemView.findViewById(R.id.photo)
        val inEmergency: ImageView = itemView.findViewById(R.id.inEmergency)
    }

    private class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.email == newItem.email &&
                    oldItem.phone == newItem.phone &&
                    oldItem.image_url == newItem.image_url &&
                    oldItem.emergency == newItem.emergency
        }
    }
}
