package com.shubham.emergencyapplication.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
        holder.nameTextView.isSelected = true
        if(!item.image_url.isNullOrEmpty()){
            Glide.with(context)
                .load(item.image_url)
                .placeholder(R.drawable.load)
                .into(holder.photoImageView)
        }
        if(item.emergency){
            Log.d("MemberAdapter", "onBindViewHolder: ${item.emergency}")
            holder.photoImageView.borderColor = context.resources.getColor(R.color.red)
            holder.inEmergency.visibility = View.VISIBLE
        }else holder.inEmergency.visibility = View.GONE

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val photoImageView: CircleImageView = itemView.findViewById(R.id.photo)
        val inEmergency: ImageView = itemView.findViewById(R.id.inEmergency)
    }

    private class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            // Unique identifier
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            // Compare all relevant fields to determine if contents are the same
            return oldItem.name == newItem.name &&
                    oldItem.email == newItem.email &&
                    oldItem.phone == newItem.phone &&
                    oldItem.image_url == newItem.image_url &&
//                    oldItem.family_members == newItem.family_members &&
                    oldItem.emergency == newItem.emergency
        }
    }

}
