package com.shubham.emergencyapplication.BottomSheets

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.checkIfUserExist
import com.shubham.emergencyapplication.Repositories.UserRepository.setUserInfo
import com.shubham.emergencyapplication.SharedPref.FamilySharedPref.getFamilyMemList
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.setProfileUpdated
import com.shubham.emergencyapplication.Utils.Constants.FAMILY_MEM

class AddmemberBottomSheetDialog : BottomSheetDialogFragment() {

//    private lateinit var nameInput: TextInputLayout
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

//        nameInput = view.findViewById(R.id.name)
        emailInput = view.findViewById(R.id.email)
//        phoneInput = view.findViewById(R.id.phone)
        addButton = view.findViewById(R.id.add)
        progressBar = view.findViewById(R.id.progressBar)

        addButton.setOnClickListener {
            handleSubmit(emailInput, progressBar, addButton, requireActivity(), this)
        }
        emailInput.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                emailInput.error = null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        return view
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetBehavior = (dialog as? BottomSheetDialog)?.behavior
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior?.peekHeight = 0

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSheetAnimation
    }

    private fun handleSubmit(
        email: TextInputLayout,
        progress: ProgressBar,
        confirm: MaterialButton,
        context: Activity,
        bottomSheetDialog: BottomSheetDialogFragment
    ) {
        val emailval = email.editText?.text.toString()

        if (emailval.isEmpty()) email.error = "Enter email"
        else
            hideProgress(progress, confirm)

        if (emailval.isNotEmpty() ) {
            if (!emailval.contains('@')) email.error = "Invalid email"
        }else
            hideProgress(progress, confirm)

        if (emailval.isNotEmpty()  && emailval.contains('@') ) {

            progress.visibility = View.VISIBLE
            confirm.isEnabled = false
            confirm.text = ""

            checkIfUserExist(emailval, context, object :ResponseCallBack<String>{
                override fun onSuccess(response: String?) {
                    if(response!!.isNotEmpty()) {
                        val map: MutableMap<String, Any> = HashMap()
                        val members = getFamilyMemList(context, FAMILY_MEM)
                        if (members.isNullOrEmpty()) {
                            map[FAMILY_MEM] = listOf(response)
                        } else {
                            if (members.contains(response)) {
                                email.error = "Member is already added"
                                hideProgress(progress, confirm)
                                return
                            } else
                                map[FAMILY_MEM] = members + response
                        }


                        setUserInfo(context, map, object : ResponseCallBack<String> {
                            override fun onSuccess(response: String?) {
                                Toast.makeText(context, "Member added successfully", Toast.LENGTH_SHORT).show()

                                hideProgress(progress, confirm)
                                bottomSheetDialog.dismiss()
                            }

                            override fun onError(error: String?) {
                                // Handle error
                                hideProgress(progress, confirm)
                                Toast.makeText(context, "Coudn't add member please try again", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    else{
                        email.error = "This member is not registered"

                        hideProgress(progress, confirm)
                    }
                }
                override fun onError(error: String?) {
                    // Handle error
                    Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show()

                    hideProgress(progress, confirm)
                }
            })


        }
    }

    private fun hideProgress(
        progress: ProgressBar,
        confirm: MaterialButton
    ) {
        progress.visibility = View.GONE
        confirm.isEnabled = true
        confirm.text = "Add Member"
    }
}
