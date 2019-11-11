package com.brandwidth.yetanothterweatherapp.mvi

import android.location.Location
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository

class MainPresenter (private val mainView: MainView,
                     private val weatherRespository: WeatherRespository,
                     private val tempretureConverter: TemperatureConverter) {

    private var hasPermission = false

    init {
        val hasLocationPermission = mainView.checkForLocationPermission()

        if (hasLocationPermission) {
            hasPermission = true
            mainView.requestLatKnownLocation()
        } else {
            mainView.requestLocationPermission()
        }

    }

    fun onUserGrantedPermission() {
        hasPermission = true
        mainView.requestLatKnownLocation()
    }

    fun onUserWantsToRefresh() {
        mainView.requestLatKnownLocation()
    }

    fun onNewLocationRetrieved(location: Location) {
        weatherRespository.getWeatherForCurrentLocation(location) {setupData(it)}
    }

    private fun setupData(cityWeather: CityWeather) {
        mainView.setLocationName(cityWeather.name)
        mainView.setWeatherTitle(cityWeather.weather.main)
        mainView.setWeatherDescription(cityWeather.weather.description)
        mainView.setWindSpeed(cityWeather.wind.speed.toString())
        mainView.setTemperature(tempretureConverter.convertKelvinToCelcius(cityWeather.main.temp).toString())
    }

}