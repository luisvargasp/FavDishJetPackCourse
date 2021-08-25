package com.tutorials.eu.favdish.model.database

import androidx.annotation.WorkerThread
import com.tutorials.eu.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val  favDishDao: FavDishDao) {


    @WorkerThread
    suspend fun insertDish(favDish: FavDish){
        favDishDao.insertFavDish(favDish)
    }
    val allDishes:Flow<List<FavDish>> = favDishDao.getAllDishes()
    val allFavouriteDishes:Flow<List<FavDish>> = favDishDao.getAllFavourites()

    @WorkerThread
    suspend fun updateDish(favDish: FavDish){
        favDishDao.updateFavDish(favDish)
    }


    @WorkerThread
    suspend fun deleteDish(favDish: FavDish){
        favDishDao.deleteDish(favDish)
    }

    fun filteredListDish(value: String): Flow<List<FavDish>> = favDishDao.getFilterDish(value)
}