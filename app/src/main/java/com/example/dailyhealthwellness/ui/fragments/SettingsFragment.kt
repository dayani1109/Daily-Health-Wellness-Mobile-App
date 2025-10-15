package com.example.dailyhealthwellness.ui.fragments

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.dailyhealthwellness.HomeFragment
import com.example.dailyhealthwellness.R

class SettingsFragment : Fragment() {

    private lateinit var switchDarkMode: Switch
    private lateinit var switchOrientation: Switch
    private lateinit var switchWidget: Switch

    private lateinit var navHome: LinearLayout
    private lateinit var navSearch: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var navSettings: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Bind views
//        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchOrientation = view.findViewById(R.id.switchOrientation)
        switchWidget = view.findViewById(R.id.switchWidget)

        navHome = view.findViewById(R.id.nav_home)
        navSearch = view.findViewById(R.id.nav_search)
        navProfile = view.findViewById(R.id.nav_profile)
        navSettings = view.findViewById(R.id.nav_settings)

//        setupDarkModeSwitch()
        setupOrientationSwitch()
        setupWidgetSwitch()
        setupNavigation()

        return view
    }

//    private fun setupDarkModeSwitch() {
//        // Set initial state based on current mode, now app ek dark moded kiyala check karala switch ek set karanva
//        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        switchDarkMode.isChecked = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
//
//        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        }
//    }

    private fun setupOrientationSwitch() {
        switchOrientation.setOnCheckedChangeListener { _, isChecked ->
            activity?.requestedOrientation = if (isChecked) {//switch ek on karam landscape
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//off- portrait
            }
        }
    }

    private fun setupWidgetSwitch() {
        switchWidget.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Home-screen Widget Enabled", Toast.LENGTH_SHORT).show()
                // TODO: Add widget enabling logic
            } else {
                Toast.makeText(requireContext(), "Home-screen Widget Disabled", Toast.LENGTH_SHORT).show()
                // TODO: Add widget disabling logic
            }
        }
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            val homeFragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, homeFragment)
                .addToBackStack(null) // optional, so user can press back
                .commit()
        }


        navProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.homeactivity_fragment_container, profileFragment)
                .addToBackStack(null) // optional, so user can press back
                .commit()
        }


        navSettings.setOnClickListener {
            Toast.makeText(requireContext(), "You are already in Settings", Toast.LENGTH_SHORT).show()
        }

    }
}
