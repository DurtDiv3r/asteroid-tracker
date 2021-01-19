package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private var _menuOption = MutableLiveData<MainFragment.MenuOption>()
    val menuOption: LiveData<MainFragment.MenuOption>
        get() = _menuOption

    val asteroids = Transformations.switchMap(menuOption) {
        when (it!!) {
            MainFragment.MenuOption.WEEK -> asteroidRepository.getAsteroids(MainFragment.MenuOption.WEEK)
            MainFragment.MenuOption.SAVED -> asteroidRepository.getAsteroids(MainFragment.MenuOption.SAVED)
            MainFragment.MenuOption.TODAY -> asteroidRepository.getAsteroids(MainFragment.MenuOption.TODAY)
            else -> asteroidRepository.getAsteroids(MainFragment.MenuOption.WEEK)
        }
    }

    val pictureOfDay = asteroidRepository.pictureOfTheDay

    init {
            viewModelScope.launch {
                _menuOption.value = MainFragment.MenuOption.WEEK
                asteroidRepository.refreshAsteroids()
                asteroidRepository.getPictureOfTheDay()
            }
    }

    fun onItemClicked(asteroid: Asteroid) {

    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class ViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    fun updateList(option: MainFragment.MenuOption) {
        _menuOption.value = option
    }
}