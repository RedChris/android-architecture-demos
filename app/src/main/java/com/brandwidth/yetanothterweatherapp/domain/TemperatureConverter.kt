package com.brandwidth.yetanothterweatherapp.domain

class TemperatureConverter  {

    private val KELVIN_CELCIUS_DIFF = 273.15

    fun convertKelvinToCelcius(tempretureInKelvin: Double) = tempretureInKelvin - KELVIN_CELCIUS_DIFF
}