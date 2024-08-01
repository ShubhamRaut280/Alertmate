package com.shubham.emergencyapplication.Ui.Fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.shubham.emergencyapplication.Adapters.ProfileItemsAdapter
import com.shubham.emergencyapplication.BottomSheets.showUpdateDetailsBottomSheet
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.ProfileItem
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getUserInfo
import com.shubham.emergencyapplication.Repositories.UserRepository.getUserInfoContinuous
import com.shubham.emergencyapplication.Repositories.UserRepository.setUserInfo
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.getUserDetails
import com.shubham.emergencyapplication.Ui.Activities.LoginActivity
import com.shubham.emergencyapplication.Utils.Constants.IMAGE_URL
import com.shubham.emergencyapplication.databinding.FragmentProfileBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private var userListener: ListenerRegistration? = null

    lateinit var binding: FragmentProfileBinding
    private val storageReference = FirebaseStorage.getInstance().reference

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
        binding.addImage.setOnClickListener {
            openGallery()
        }


    }


    private fun setUpProfile() {

        binding.pb.visibility = View.VISIBLE
        userListener = getUserInfoContinuous(requireContext(), object : ResponseCallBack<User> {
            override fun onSuccess(data: User?) {
                binding.pb.visibility = View.GONE

                if (data != null) {
                    binding.name.text = data.name
                    binding.email.text = data.email
                    binding.phone.text = data.phone.toString()
                    val url = data.image_url
                    if(!url.isNullOrEmpty()){
                        Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.image_progress_animation)
                            .into(binding.profileImg)
                    }
                }
            }

            override fun onError(message: String?) {
                // Handle error
                binding.pb.visibility = View.GONE
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        userListener?.remove()
    }

    private fun setUpOptionsRecycler() {
        val itemsList  = mutableListOf(

            ProfileItem(R.drawable.edit, "Edit profile"),
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
        showUpdateDetailsBottomSheet(requireActivity(), FirebaseAuth.getInstance(), true)
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

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(galleryIntent)
    }

    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(cameraIntent)
    }

    private val cameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                val uri = getImageUri(it)
                uploadImageToFirebase(uri)
            }
        }
    }


    private fun getImageUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver, bitmap, "Title", null
        )
        return Uri.parse(path)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        binding.pb.visibility = View.VISIBLE
        val fileName = FirebaseAuth.getInstance().currentUser?.uid ?: "${System.currentTimeMillis()}.jpg"
        Log.d("filename", "${fileName} this is file name ${System.currentTimeMillis()}")
        val path = "Users/profileImages/$fileName"

        val fileRef = storageReference.child(path)

        fileRef.putFile(uri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { downloadUri ->

                    val imageUrl = downloadUri.toString()
                    Log.d("Firebase", "Image URL: $imageUrl")
                    setUserInfo(requireContext(), mapOf("image_url" to imageUrl),object :ResponseCallBack<String> {
                        override fun onSuccess(data: String?) {
                            Toast.makeText(requireContext(), "Image updated successfully!", Toast.LENGTH_SHORT).show()
                            binding.pb.visibility = View.GONE
                        }
                        override fun onError(message: String?) {
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            binding.pb.visibility = View.GONE
                        }
                    })
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                binding.pb.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to upload image, please try again!", Toast.LENGTH_SHORT).show()
            }
    }
}