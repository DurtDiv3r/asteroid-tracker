package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidsDatabase) {

    /**
     * A list of asteroids that can be shown on the screen.
    //     */
//    val asteroids: LiveData<List<Asteroid>> =
//            Transformations.map(database.asteroidDao.getAsteroids()) {
//                it.asDomainModel()
//            }

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

                // Testing
                val jsonResult = Network.service.getAsteroidList(Constants.API_KEY)

                val asteroids = parseAsteroidsJsonResult(JSONObject(jsonResult))
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
                Log.d("REPOSITORY", "PAUSE")
            } catch (e: Exception) {
                Log.d("REPOSITORY", "Unable to RefreshAsteroids: " + e.message)
            }
        }
    }
}