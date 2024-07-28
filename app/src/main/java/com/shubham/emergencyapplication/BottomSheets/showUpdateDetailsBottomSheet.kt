package com.shubham.emergencyapplication.BottomSheets

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getUserInfo
import com.shubham.emergencyapplication.Repositories.UserRepository.setUserInfo
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.setProfileUpdated
import com.shubham.emergencyapplication.Ui.Activities.DashboardActivity
import de.hdodenhof.circleimageview.CircleImageView

fun showUpdateDetailsBottomSheet(context: Activity, auth: FirebaseAuth, isRemovable : Boolean ) {
        try {
            val bottomSheetDialog = BottomSheetDialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.update_details, null)

            bottomSheetDialog.setContentView(view)

            val confirm = view.findViewById<MaterialButton>(R.id.add)
            val email = view.findViewById<TextInputLayout>(R.id.email)
            val name = view.findViewById<TextInputLayout>(R.id.name)
            val phone = view.findViewById<TextInputLayout>(R.id.phone)
            val progress = view.findViewById<ProgressBar>(R.id.progressBar)

            if (!isRemovable) {
                bottomSheetDialog.setCancelable(false)
                bottomSheetDialog.setCanceledOnTouchOutside(false)
            }

            email.editText?.setText(auth.currentUser?.email)
            email.editText?.isEnabled = false

            getUserInfo(context, object : ResponseCallBack<User> {
                override fun onSuccess(response: User?) {
                    if (response != null) {
//                        email.editText?.setText(response.email)
                        name.editText?.setText(response.name)
                        phone.editText?.setText("${if(response.phone == null) "" else response.phone}")
                    }
                }
                override fun onError(error: String?) {
                    // Handle error
                }
            })
            email.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    email.error = null
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            confirm.setOnClickListener { v: View? ->
                handleSubmit(email, name, phone, progress, confirm, context, bottomSheetDialog)
            }

            bottomSheetDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("dialog", "$e.message")
        }
    }

private fun handleSubmit(
    email: TextInputLayout,
    name: TextInputLayout,
    phone: TextInputLayout,
    progress: ProgressBar,
    confirm: MaterialButton,
    context: Activity,
    bottomSheetDialog: BottomSheetDialog
) {
    val emailval = email.editText?.text.toString()
    val nameval = name.editText?.text.toString()
    val phoneval = phone.editText?.text.toString()

    if (emailval.isEmpty()) email.error = "Enter email"
    if (nameval.isEmpty()) name.error = "Enter name"
    if (phoneval.isEmpty()) phone.error = "Enter phone number"

    if (emailval.isNotEmpty() && nameval.isNotEmpty() && phoneval.isNotEmpty()) {
        if (!emailval.contains('@')) email.error = "Invalid email"
        if (phoneval.length != 10) phone.error = "Invalid phone number"
    }

    if (emailval.isNotEmpty() && nameval.isNotEmpty() && phoneval.isNotEmpty() && emailval.contains(
            '@'
        ) && phoneval.length == 10
    ) {
        val map = mapOf<String, Any>(
            "email" to emailval,
            "name" to nameval,
            "phone" to phoneval.toLong(),
            "emergency" to false
        )
        progress.visibility = View.VISIBLE
        confirm.isEnabled = false
        confirm.text = ""

        setUserInfo(context, map, object : ResponseCallBack<String> {
            override fun onSuccess(response: String?) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                setProfileUpdated(context, true)
//                            context.startActivity(Intent(context, DashboardActivity::class.java))
                bottomSheetDialog.dismiss()
            }

            override fun onError(error: String?) {
                // Handle error
                progress.visibility = View.GONE
                confirm.isEnabled = true
                confirm.text = "Update Details"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })
    }
}





