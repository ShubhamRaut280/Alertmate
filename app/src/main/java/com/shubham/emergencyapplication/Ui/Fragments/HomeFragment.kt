package com.shubham.emergencyapplication.Ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.activity.viewModels
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.Adapters.HomeNotificationAdapter
import com.shubham.emergencyapplication.Adapters.MemberAdapter
import com.shubham.emergencyapplication.BottomSheets.DialogUtils.showUserDetailsSheet
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.UserRepository.getFamilyMembers
import com.shubham.emergencyapplication.SharedPref.UserDataSharedPref.getUserDetails
import com.shubham.emergencyapplication.Utils.Constants.IMAGE_URL
import com.shubham.emergencyapplication.Utils.Constants.NAME
import com.shubham.emergencyapplication.Utils.DateUtils.greetBasedOnTime
import com.shubham.emergencyapplication.ViewModels.HomeFragmentViewModel
import com.shubham.emergencyapplication.databinding.ActivityDashboardBinding
import com.shubham.emergencyapplication.databinding.FragmentHomeBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var adapter: MemberAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)

//        val notifications = listOf("Please give location permission", "please give camera permsission")
//        setupNotificationRecycler(notifications)
        setUpMemberRecycler()
    }

    fun setUpMemberRecycler(){

        binding.memberRecycler.layoutManager = GridLayoutManager(requireContext(), 4) // 2 columns
        adapter = MemberAdapter(requireContext(), onItemClick = { user ->
            showUserDetailsSheet(user, requireContext(), parentFragmentManager)
        })
        binding.memberRecycler.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner, Observer { users ->
            val name = getUserDetails(requireContext(), NAME)
            val img = getUserDetails(requireContext(), IMAGE_URL)
            binding.name.text = "Hello $name"
            if(!img.isNullOrEmpty())Glide.with(requireContext()).load(img).into(binding.myprofileImg)
            adapter.submitList(users)
            binding.timeMsg.text = greetBasedOnTime()
        })


    }

    private fun setupNotificationRecycler(notifications: List<String>) {
        binding.notificationRecycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = HomeNotificationAdapter(notifications) { userName ->
            // Handle the click event, e.g., show a Toast or start a new Activity
            Toast.makeText(requireContext(), "Clicked on: $userName", Toast.LENGTH_SHORT).show()
        }
        binding.notificationRecycler.adapter = adapter

    }
}