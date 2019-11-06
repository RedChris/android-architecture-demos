package com.brandwidth.yetanothterweatherapp.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Main (val temp: Double)