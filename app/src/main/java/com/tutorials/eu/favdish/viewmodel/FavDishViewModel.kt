package com.tutorials.eu.favdish.viewmodel

import androidx.lifecycle.*
import com.tutorials.eu.favdish.model.database.FavDishRepository
import com.tutorials.eu.favdish.model.entities.FavDish
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FavDishViewModel(private val repository: FavDishRepository):ViewModel() {
    fun insert(favDish: FavDish)=viewModelScope.launch {

        repository.insertDish(favDish)

    }

    val allDishes:LiveData<List<FavDish>> = repository.allDishes.asLiveData()
    val allFavourites:LiveData<List<FavDish>> = repository.allFavouriteDishes.asLiveData()


    fun getFilteredList(value :String):LiveData<List<FavDish>> = repository.filteredListDish(value).asLiveData()




    fun update(favDish: FavDish)=viewModelScope.launch {

        repository.updateDish(favDish)

    }


    fun delete(favDish: FavDish)=viewModelScope.launch {

        repository.deleteDish(favDish)

    }

}

class FavDishViewModelFactory(private val repository: FavDishRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){


            return FavDishViewModel(repository) as T


        }
        throw IllegalArgumentException("unknown model class")



    }


}