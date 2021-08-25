package com.tutorials.eu.favdish.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tutorials.eu.favdish.model.entities.RandomDish
import com.tutorials.eu.favdish.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel :ViewModel(){
    private val randomRecipeDishAPI=RandomDishApiService()

    private val compositeDisposable=CompositeDisposable()

    val loadRandomDish=MutableLiveData<Boolean>()
    val randomDishResponse=MutableLiveData<RandomDish.Recipes>()

    val randomDishLoadingError=MutableLiveData<Boolean>()

    fun getRandomFromAPI(){
        loadRandomDish.value=true
        compositeDisposable.add(

            randomRecipeDishAPI.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<RandomDish.Recipes>(){

                    override fun onSuccess(t: RandomDish.Recipes?) {
                        loadRandomDish.value=false
                        randomDishResponse.value=t!!
                        randomDishLoadingError.value=false
                    }

                    override fun onError(e: Throwable?) {
                        loadRandomDish.value=false
                        randomDishLoadingError.value=true                    }

                }))

    }



}