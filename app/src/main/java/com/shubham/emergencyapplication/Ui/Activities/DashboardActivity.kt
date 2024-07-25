package com.shubham.emergencyapplication.Ui.Activities

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shubham.emergencyapplication.BottomSheets.DialogUtils.showSosBottomSheetDialog
import com.shubham.emergencyapplication.R
import com.shubham.emergencyapplication.Repositories.FamilyRepository.saveFamilyMembers
import com.shubham.emergencyapplication.Ui.Fragments.HomeFragment
import com.shubham.emergencyapplication.Ui.Fragments.MapFragment
import com.shubham.emergencyapplication.Ui.Fragments.ProfileFragment
import com.shubham.emergencyapplication.Utils.DraggableUtils.makeViewDraggable
import com.shubham.emergencyapplication.Utils.UtilityFuns.handleAdjustResizeForKeyboard
import com.shubham.emergencyapplication.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        bottomNavigationView = binding.bottomNavigation

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }

    private fun init() {
        saveFamilyMembers(this)

        makeViewDraggable(binding.addPerson)

        binding.addPerson.setOnClickListener {
            showSosBottomSheetDialog(this, supportFragmentManager)
        }
        window.statusBarColor = resources.getColor(R.color.main)

        setupViewPager()
        setupBottomNavigation()

        handleAdjustResizeForKeyboard(binding.root)
    }

    private fun setupViewPager() {
        val adapter = FragmentAdapter(this)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.map -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.profile -> {
                    viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        return true
    }

    private inner class FragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> MapFragment()
                2 -> ProfileFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

}
