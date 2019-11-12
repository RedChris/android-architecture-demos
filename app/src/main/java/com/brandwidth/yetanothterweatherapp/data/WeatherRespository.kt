package com.brandwidth.yetanothterweatherapp.data

import android.location.Location
import com.brandwidth.yetanothterweatherapp.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class WeatherRespository  {

    private val client = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()

    fun getWeatherForCurrentLocation(location: Location, callback: (CityWeather)-> Unit) {
        val request = buildRequest(location)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                parseResponse(response, callback)
            }

        })
    }

    private val locationSource = BehaviorSubject.create<CityWeather>()

    fun getWeatherForCurrentLocation(location: Location): Observable<CityWeather> {

        val request = buildRequest(location)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                parseResponse(response) {}
            }

        })
        return locationSource

    }

    private fun parseResponse(response: Response, callback: (CityWeather)-> Unit) {
        val adapter: JsonAdapter<CityWeather> = moshi.adapter(
            CityWeather::class.java)
        val cityWeather = adapter.fromJson(response.body!!.toString())

        cityWeather?.let {
            callback(it)
            locationSource.onNext(cityWeather!!)
        }
    }

    private fun buildRequest(location: Location) : Request {
        val httpBuilder = Constants.ENDPOINT.toHttpUrlOrNull()!!.newBuilder()
        getParams(location).forEach {httpBuilder.addQueryParameter(it.key, it.value)}
        return Request.Builder().url(httpBuilder.build()).build()
    }

    private fun getParams(location: Location): Map<String, String> {
        val params = HashMap<String, String>()
        params[Constants.APIKEY_PARAM] = Constants.APIKEY
        params[Constants.LAT_LOCATION_PARAM] = location.latitude.toString()
        params[Constants.LONG_LOCATION_PARAM] = location.longitude.toString()

        return params
    }
}