package com.shubham.emergencyapplication.BottomSheets

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.hdodenhof.circleimageview.CircleImageView
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R

class UserDetailsBottomSheet(private val userId: User) : BottomSheetDialogFragment() {

    private lateinit var name: TextView
    private lateinit var phone: TextView
    private lateinit var email: TextView
    private lateinit var profileImg: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_details_bottomsheet, container, false)
        view.background = resources.getDrawable(R.drawable.sheet_corner)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        name = view.findViewById(R.id.name)
        phone = view.findViewById(R.id.phone)
        email = view.findViewById(R.id.email)
        profileImg = view.findViewById(R.id.profileImg)

        showDetails(userId)

        return view
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = (dialog as? BottomSheetDialog)?.behavior

        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED // Start collapsed state
        bottomSheetBehavior?.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO // Automatically adjust peek height

        // Set dragging behavior
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // You can add additional actions here based on the state
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // This will be called when the bottom sheet is dragged
            }
        })
    }

    private fun showDetails(user: User) {
        name.text = user.name
        phone.text = user.phone.toString()
        email.text = user.email
        if (!user.image_url.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(user.image_url)
                .placeholder(R.drawable.loader)
                .into(profileImg)
        }
    }
}
