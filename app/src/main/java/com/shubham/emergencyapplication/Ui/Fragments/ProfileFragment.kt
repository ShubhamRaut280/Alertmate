package com.shubham.emergencyapplication.Ui.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.Adapters.ProfileItemsAdapter
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.ProfileItem
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getUserInfo
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
        setUpOptionsRecycler()
        setUpProfile()


    }

    private fun setUpProfile() {
        if(FirebaseAuth.getInstance().currentUser!=null){
            getUserInfo(requireContext(), object :ResponseCallBack<User> {
                override fun onSuccess(data: User?) {
                    if (data != null) {
                        binding.name.text = data.name
                        binding.email.text = data.email
                        binding.phone.text = data.phone.toString()

                        if(!data.image_url.isNullOrEmpty()){

                        }
                    }
                }

                override fun onError(message: String?) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun setUpOptionsRecycler() {
        val itemsList  = mutableListOf(

            ProfileItem(R.drawable.edit, "Edit profile"),
            ProfileItem(R.drawable.family, "Family members"),
            ProfileItem(R.drawable.logout, "Logout")
        )
        binding.profileItemsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.profileItemsRecycler.adapter = ProfileItemsAdapter(
            requireContext(),
            itemsList
        ){
            when(it.name){
                "Logout" -> showLogout()
                "Edit profile" -> editProfile()
                "Family members" -> showFamilyMembers()
            }
        }
    }

    private fun editProfile() {
        Toast.makeText(requireContext(), "Edit profile", Toast.LENGTH_SHORT).show()
    }

    private fun showFamilyMembers() {
    Toast.makeText(requireContext(), "Family members", Toast.LENGTH_SHORT).show()
    }

    fun showLogout() {
        try {
            val dialogToCreateAlert = Dialog(requireContext())
            dialogToCreateAlert.setContentView(R.layout.logout_dialog)

            dialogToCreateAlert.window?.setBackgroundDrawableResource(R.drawable.round_corner_card_bg)
            dialogToCreateAlert.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Find views
            val confirm = dialogToCreateAlert.findViewById<MaterialButton>(R.id.logout)
            val cancel = dialogToCreateAlert.findViewById<MaterialButton>(R.id.cancel)
            val title = dialogToCreateAlert.findViewById<TextView>(R.id.title)

            // Ensure title text is visible
            title.textAlignment = TEXT_ALIGNMENT_CENTER

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