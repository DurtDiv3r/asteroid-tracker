package com.udacity.asteroidradar.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface AsteroidService {

    //todo add all params
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidList(
            @Query("api_key") apiKey: String): String
}

interface PictureService {
    //todo check if call is correct
    @GET("planetary/apod")
    suspend fun getNewDailyPicture(
            @Query("api_key") apiKey: String): NetworkPictureOfTheDay
}

object Network {
    private val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val asteroidService = retrofit.create(AsteroidService::class.java)
    val imageService = retrofit.create(PictureService::class.java)
}
