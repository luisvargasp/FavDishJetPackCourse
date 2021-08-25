package com.tutorials.eu.favdish.view.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.tutorials.eu.favdish.R
import com.tutorials.eu.favdish.databinding.ActivityMainBinding
import com.tutorials.eu.favdish.model.notification.NotifyWorker
import com.tutorials.eu.favdish.utils.Constants
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
         navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.all_dishes, R.id.favourite_dishes, R.id.random_dish
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(intent.hasExtra(Constants.NOTIFICATION_ID)){

            val notificationId=intent.getIntExtra(Constants.NOTIFICATION_ID,0)
            binding.navView.selectedItemId=R.id.random_dish

        }

        startWork()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController,null)
    }

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun createWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(15,TimeUnit.MINUTES)
        .setConstraints(createConstraints())
        .build()


    private fun startWork(){
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Fav Dish notification",ExistingPeriodicWorkPolicy.KEEP,createWorkRequest())

    }



    fun hideBottomNav(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration=300
    }
    fun showBottomNav(){
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration=300
    }
}