package com.example.myphone.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myphone.R
import com.example.myphone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Tìm cái khung NavHostFragment từ giao diện
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment

        // 2. Lấy bộ điều khiển (Controller) của nó
        val navController = navHostFragment.navController

        // 3. Kết nối BottomNavigation với NavController
        binding.bottomNav.setupWithNavController(navController)
    }
}