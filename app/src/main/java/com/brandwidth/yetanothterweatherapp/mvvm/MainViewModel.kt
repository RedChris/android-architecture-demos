package com.brandwidth.yetanothterweatherapp.mvvm

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository

class MainViewModel (application: Application) : AndroidViewModel(application) {

    private val temperatureConverter = TemperatureConverter()
    private val weatherRepository = WeatherRespository()
    private var hasPermission = false

    val locationName = MutableLiveData<String>()
    val weatherTitle = MutableLiveData<String>()
    val weatherDescription = MutableLiveData<String>()
    val windSpeed = MutableLiveData<String>()
    val temperature = MutableLiveData<String>()
    val checkForLocationPermissionEvent = SingleLiveEvent<Unit>()
    val requestLocationPermissionEvent = SingleLiveEvent<Unit>()
    val requestLastKnownLocationEvent = SingleLiveEvent<Unit>()

    init {
        checkForLocationPermissionEvent.call()
    }

    fun onHasLocationPermission(hasLocationPermission: Boolean) {
        if (hasLocationPermission) {
            hasPermission = true
            requestLastKnownLocationEvent.call()
        } else {
            requestLocationPermissionEvent.call()
        }
    }

    fun onUserGrantedPermission() {
        hasPermission = true
        requestLastKnownLocationEvent.call()
    }

    fun onUserWantsToRefresh() {
        requestLastKnownLocationEvent.call()
    }

    fun onNewLocationRetrieved(location: Location) {
        weatherRepository.getWeatherForCurrentLocation(location) {setupData(it)}
    }

    private fun setupData(cityWeather: CityWeather) {
        locationName.value = cityWeather.name
        weatherTitle.value = cityWeather.weather.main
        weatherDescription.value = cityWeather.weather.description
        windSpeed.value = cityWeather.wind.speed.toString()
        temperature.value = temperatureConverter.convertKelvinToCelcius(cityWeather.main.temp).toString()
    }

}