package com.shubham.emergencyapplication.BottomSheets

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.R.style.BottomSheetAnimation
import com.shubham.emergencyapplication.Repositories.FamilyRepository.saveFamilyMembers

class AddmemberBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var nameInput: TextInputLayout
    private lateinit var emailInput: TextInputLayout
    private lateinit var phoneInput: TextInputLayout
    private lateinit var addButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_member_form, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        nameInput = view.findViewById(R.id.name)
        emailInput = view.findViewById(R.id.email)
        phoneInput = view.findViewById(R.id.phone)
        addButton = view.findViewById(R.id.add)
        progressBar = view.findViewById(R.id.progressBar)

        addButton.setOnClickListener {
            handleAddMember()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = (dialog as? BottomSheetDialog)?.behavior
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior?.peekHeight = 0

        dialog?.window?.attributes?.windowAnimations = BottomSheetAnimation
    }

    private fun handleAddMember() {
        saveFamilyMembers(requireContext())
        showProgress()
        view?.postDelayed(
            {
                hideProgress()
                Toast.makeText(requireContext(), "Member added successfully", Toast.LENGTH_SHORT).show()
                dismiss()
            }, 4000
        )
    }

    private fun showProgress() {
        progressBar.visibility = View.VISIBLE
        addButton.text = ""
        addButton.isEnabled = false
    }

    private fun hideProgress() {
        progressBar.visibility = View.GONE
        addButton.text = "Add member"
        addButton.isEnabled = true
    }
}
