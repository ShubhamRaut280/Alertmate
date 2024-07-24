package com.shubham.emergencyapplication.Ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.shubham.emergencyapplication.Adapters.HomeNotificationAdapter
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.databinding.ActivityDashboardBinding
import com.shubham.emergencyapplication.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
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

        val notifications = listOf("Please give location permission", "please give camera permsission")
        setupNotificationRecycler(notifications)
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