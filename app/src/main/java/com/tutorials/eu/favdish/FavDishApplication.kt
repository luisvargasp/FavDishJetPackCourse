package com.tutorials.eu.favdish

import android.app.Application
import com.tutorials.eu.favdish.model.database.FavDishDatabase
import com.tutorials.eu.favdish.model.database.FavDishRepository

class FavDishApplication:Application() {

    private val database by lazy {
        FavDishDatabase.getDatabase(this)

    }


    val repository by lazy {
        FavDishRepository(database.wordDao())

    }
}