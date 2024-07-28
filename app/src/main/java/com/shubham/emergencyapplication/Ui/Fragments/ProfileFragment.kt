package com.shubham.emergencyapplication.Ui.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Ui.Activities.LoginActivity
import com.shubham.emergencyapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {


    }
    fun showLogout() {
        try {
            val dialogToCreateAlert = Dialog(requireContext())
            dialogToCreateAlert.setContentView(R.layout.logout_dialog)

            dialogToCreateAlert.window!!.setBackgroundDrawableResource(R.drawable.round_corner_card_bg)
            dialogToCreateAlert.window!!
                .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)


            val confirm = dialogToCreateAlert.findViewById<MaterialButton>(R.id.logout)
            val cancel = dialogToCreateAlert.findViewById<MaterialButton>(R.id.cancel)

            confirm.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                dialogToCreateAlert.dismiss()

            }
            cancel.setOnClickListener {
                dialogToCreateAlert.dismiss()
            }

            dialogToCreateAlert.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}