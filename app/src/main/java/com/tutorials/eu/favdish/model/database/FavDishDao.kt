package com.tutorials.eu.favdish.model.database

import androidx.room.*
import com.tutorials.eu.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavDish(dish :FavDish)

    @Query("SELECT * FROM fav_dishes_table ORDER BY ID")
    fun getAllDishes(): Flow<List<FavDish>>


    @Update
   suspend fun updateFavDish(dish: FavDish)

    @Delete
    suspend fun deleteDish(dish: FavDish)



    @Query("SELECT * FROM fav_dishes_table WHERE favourite_dish=1  ORDER BY ID")
    fun getAllFavourites(): Flow<List<FavDish>>

    @Query("SELECT * FROM fav_dishes_table WHERE type=:filter  ORDER BY ID")
    fun getFilterDish(filter:String): Flow<List<FavDish>>



}