package com.tutorials.eu.favdish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tutorials.eu.favdish.model.entities.FavDish

@Database(entities = [FavDish::class],version = 1)
abstract class FavDishDatabase:RoomDatabase() {
    abstract fun wordDao(): FavDishDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FavDishDatabase? = null

        fun getDatabase(context: Context): FavDishDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}