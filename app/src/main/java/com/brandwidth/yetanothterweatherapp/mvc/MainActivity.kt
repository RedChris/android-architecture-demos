package com.brandwidth.yetanothterweatherapp.mvc

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brandwidth.yetanothterweatherapp.R
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val LOCATION_CODE_REQUEST = 200
    private val tempretureConverter = TemperatureConverter()
    private val WeatherModel = WeatherModel()

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
                WeatherModel.getWeatherForCurrentLocation(location!!) {setData(it)}
            }
    }

    private fun setData(cityWeather: CityWeather) {
        locationName.text = cityWeather.name
        weather.text = cityWeather.weather.main
        weatherDescription.text = cityWeather.weather.description
        windSpeed.text = cityWeather.wind.speed.toString()
        tempreture.text = tempretureConverter.convertKelvinToCelcius(cityWeather.main.temp).toString()
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
