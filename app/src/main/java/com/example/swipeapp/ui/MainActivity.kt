package com.example.swipeapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.swipeapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       installSplashScreen()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.
        findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

    }
}