package com.brandwidth.yetanothterweatherapp.data

import com.squareup.moshi.JsonClass;

@JsonClass(generateAdapter = true)
class Weather (val main: String, val description: String)