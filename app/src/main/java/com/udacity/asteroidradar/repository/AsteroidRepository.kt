package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.MainFragment
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidsDatabase) {

    val pictureOfTheDay = Transformations.map(database.asteroidDao.getTodayPicture()) {
        it?.asDomainModel()
    }

    suspend fun getPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay = Network.imageService.getNewDailyPicture(Constants.API_KEY)
                database.asteroidDao.insertPictureOfTheDay(pictureOfDay.asDatabaseModel())
            } catch (e: java.lang.Exception) {
                Log.e("ASTEROID REPOSITORY","Unable to get new daily picture: " + e.message)
            }
        }
    }

    /**
     * A list of asteroids that can be shown on the screen.
    //     */
//    val asteroids: LiveData<List<Asteroid>> =
//            Transformations.map(database.asteroidDao.getAsteroids()) {
//                it.asDomainModel()
//            }

    fun getAsteroids(menuOption: MainFragment.MenuOption): LiveData<List<Asteroid>> {
        return when (menuOption) {
            MainFragment.MenuOption.WEEK -> Transformations.map(database.asteroidDao.getWeekAsteroid()) {
                it.asDomainModel()

            }

            MainFragment.MenuOption.TODAY -> Transformations.map(database.asteroidDao.getTodayAsteroid()) {
                it.asDomainModel()

            }

            MainFragment.MenuOption.SAVED -> Transformations.map(database.asteroidDao.getSavedAsteroid()) {
                it.asDomainModel()

            }

//            else -> Transformations.map(database.asteroidDao.getAsteroids()) {
//                it.asDomainModel()
            else -> Transformations.map(database.asteroidDao.getWeekAsteroid()) {
                it.asDomainModel()


            }
        }

    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val jsonResult = Network.asteroidService.getAsteroidList(Constants.API_KEY)
                val asteroids = parseAsteroidsJsonResult(JSONObject(jsonResult))
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            } catch (e: Exception) {
                Log.d("REPOSITORY", "Unable to RefreshAsteroids: " + e.message)
            }
        }
    }
}