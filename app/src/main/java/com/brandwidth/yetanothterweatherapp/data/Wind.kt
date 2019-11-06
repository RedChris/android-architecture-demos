package com.brandwidth.yetanothterweatherapp.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Wind (val speed: Double)