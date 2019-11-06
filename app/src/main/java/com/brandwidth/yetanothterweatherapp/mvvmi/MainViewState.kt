package com.brandwidth.yetanothterweatherapp.mvvmi

import com.brandwidth.yetanothterweatherapp.data.CityWeather

sealed class MainViewState {
    object UnknownPermissionState : MainViewState()
    object NeedsPermissionState : MainViewState()
    data class DisplayLocationState(val cityWeather: CityWeather) : MainViewState()
    data class RefreshLocationState(val cityWeather: CityWeather?) : MainViewState()
}