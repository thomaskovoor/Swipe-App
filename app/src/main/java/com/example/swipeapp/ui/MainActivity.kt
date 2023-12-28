package com.example.swipeapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.swipeapp.R

/**
 * This activity is the entry point of the application. It sets up a splash screen and hosts a NavHostFragment that manages navigation within the app.
 *
 * @property navController The NavController that manages app navigation.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    /**
     * Called when the activity is starting.
     *
     * This method installs a splash screen, sets the content view to the activity_main layout, and initializes the NavController.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       installSplashScreen()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.
        findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

    }
}