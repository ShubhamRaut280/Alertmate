package com.shubham.emergencyapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.databinding.NotificationRecyclerItemBinding

class HomeNotificationAdapter(
    private val userList: List<String>,
    private val onItemClick: (String) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<HomeNotificationAdapter.HomeNotificationViewHolder>() {

    inner class HomeNotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NotificationRecyclerItemBinding.bind(itemView)

        init {
            // Set up the click listener for the item view
            itemView.setOnClickListener {
                val userName = binding.userName.text.toString()
                onItemClick(userName)
            }
        }

        fun bind(msg: String) {
            binding.userName.text = msg
            binding.userName.isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeNotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_recycler_item, parent, false)
        return HomeNotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeNotificationViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
