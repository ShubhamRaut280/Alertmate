package com.shubham.emergencyapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.shubham.emergencyapplication.Models.ProfileItem
import com.shubham.emergencyapplication.R

class ProfileItemsAdapter(
    private val context: Context,
    private val itemsList: List<ProfileItem>,
    private val itemClickListener: (ProfileItem) -> Unit
) : RecyclerView.Adapter<ProfileItemsAdapter.ProfileItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_list_item, parent, false)
        return ProfileItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileItemsViewHolder, position: Int) {
        val item = itemsList[position]
        holder.name.text = item.name
        holder.name.isSelected = true
        holder.icon.icon = AppCompatResources.getDrawable(context, item.icon)

        holder.main.setOnClickListener {
            itemClickListener(item)
        }

    }

    override fun getItemCount(): Int = itemsList.size

    inner class ProfileItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: MaterialButton = itemView.findViewById(R.id.itemIcon)
        val name: TextView = itemView.findViewById(R.id.itemName)
        val main: RelativeLayout = itemView.findViewById(R.id.main)
    }
}
