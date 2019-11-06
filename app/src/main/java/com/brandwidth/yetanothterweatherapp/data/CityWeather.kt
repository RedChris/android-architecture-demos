package com.brandwidth.yetanothterweatherapp.data;

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CityWeather (val name: String, val main: Main, val wind: Wind, val weather: Weather)