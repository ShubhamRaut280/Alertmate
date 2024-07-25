package com.shubham.emergencyapplication.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getUserInfo
import com.shubham.emergencyapplication.Repositories.UserRepository.setUserInfo
import com.shubham.emergencyapplication.Ui.Activities.DashboardActivity


object DialogUtils {
    fun showUpdateDetailsDialog(context: Activity, auth: FirebaseAuth) {
        try {
            val dialogToCreateAlert = Dialog(context)
            dialogToCreateAlert.setContentView(R.layout.update_details)

            dialogToCreateAlert.window!!.setBackgroundDrawableResource(R.drawable.round_corner_card_bg)
            dialogToCreateAlert.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)


            val confirm = dialogToCreateAlert.findViewById<MaterialButton>(R.id.add)
            val email = dialogToCreateAlert.findViewById<TextInputLayout>(R.id.email)
            val name = dialogToCreateAlert.findViewById<TextInputLayout>(R.id.name)
            val phone = dialogToCreateAlert.findViewById<TextInputLayout>(R.id.phone)
            val progress = dialogToCreateAlert.findViewById<ProgressBar>(R.id.progressBar)

            dialogToCreateAlert.setCancelable(false)
            dialogToCreateAlert.setCanceledOnTouchOutside(false)

            email.editText?.setText(auth.currentUser?.email)
            email.editText?.isEnabled = false


            getUserInfo(context, object : ResponseCallBack<User>{
                override fun onSuccess(response: User?) {
                    if(response != null) {
                        email.editText?.setText(response.email)
                        name.editText?.setText(response.name)
                        phone.editText?.setText("$response.phone")
                    }

                }
                override fun onError(error: String?) {
                    // Handle error
                }
            })



            confirm.setOnClickListener { v: View? ->


                val emailval = email.editText?.text.toString()
                val nameval = name.editText?.text.toString()
                val phoneval = phone.editText?.text.toString()
                if (emailval.isEmpty()) email.error = "Enter email"
                if (nameval.isEmpty()) name.error = "Enter name"
                if (phoneval.isEmpty()) phone.error = "Enter phone number"
                if (emailval.isNotEmpty() && nameval.isNotEmpty() && phoneval.isNotEmpty()) {
                    if(!emailval.contains('@'))email.error = "Invalid email"
                    if(phoneval.length != 10) phone.error = "Invalid phone number"
                }
                if (emailval.isNotEmpty() && nameval.isNotEmpty() && phoneval.isNotEmpty() && emailval.contains('@') && phoneval.length == 10) {
                    val map = mapOf<String, Any>(
                        "email" to emailval,
                        "name" to nameval,
                        "phone" to phoneval
                    )
                    progress.visibility = View.VISIBLE
                    confirm.isEnabled = false
                    confirm.text = ""

                    setUserInfo(context, map, object : ResponseCallBack<String>{
                        override fun onSuccess(response: String?) {
                            Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, DashboardActivity::class.java))
                            dialogToCreateAlert.dismiss()
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
            dialogToCreateAlert.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("dialog", "$e.message")
        }
    }
}