package com.brandwidth.yetanothterweatherapp.mvp

interface MainView {
    fun checkForLocationPermission(): Boolean
    fun requestLatKnownLocation()
    fun requestLocationPermission()
    fun setLocationName(name: String)
    fun setWeatherTitle(title: String)
    fun setWeatherDescription(description: String)
    fun setWindSpeed(speed: String)
    fun setTemperature(temperature: String)
}
