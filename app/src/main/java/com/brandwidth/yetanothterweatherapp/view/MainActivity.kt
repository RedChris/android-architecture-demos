package com.brandwidth.yetanothterweatherapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brandwidth.yetanothterweatherapp.Constants
import com.brandwidth.yetanothterweatherapp.R
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val LOCATION_CODE_REQUEST = 200
    private val client = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()
    private val tempretureConverter = TemperatureConverter()

    private var hasPermission = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val hasLocationPermission = checkForPermission()

        if (hasLocationPermission) {
            hasPermission = true
            getWeatherForCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_CODE_REQUEST)
        }

        swiperefresh.setOnRefreshListener { if (hasPermission) getWeatherForCurrentLocation() }
    }

    private fun getWeatherForCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->

                val request = buildRequest(location!!)
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        parseResponse(response)
                    }

                })

            }
    }

    private fun parseResponse(response: Response) {
        val adapter: JsonAdapter<CityWeather>  = moshi.adapter(
            CityWeather::class.java)
        val cityWeather = adapter.fromJson(response.body!!.toString())
        cityWeather?.let {setData(it)}
    }

    private fun setData(cityWeather: CityWeather) {
        locationName.text = cityWeather.name
        weather.text = cityWeather.weather.main
        weatherDescription.text = cityWeather.weather.description
        windSpeed.text = cityWeather.wind.speed.toString()
        tempreture.text = tempretureConverter.convertKelvinToCelcius(cityWeather.main.temp).toString()
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

    private fun checkForPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_CODE_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    hasPermission = true
                    getWeatherForCurrentLocation()
                } else {
                    //boo
                }
                return
            }
        }
    }

}
