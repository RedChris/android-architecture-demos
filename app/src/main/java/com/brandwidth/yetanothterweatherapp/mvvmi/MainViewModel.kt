package com.brandwidth.yetanothterweatherapp.mvvmi

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository

class MainViewModel (application: Application) : AndroidViewModel(application) {

    private var cityWeather : CityWeather? = null
    private val weatherRepository = WeatherRespository()
    private var hasPermission = false

    val state = MutableLiveData<MainViewState>()

    init {
        state.value = MainViewState.UnknownPermissionState
    }

    fun onHasLocationPermission(hasLocationPermission: Boolean) {
        if (hasLocationPermission) {
            hasPermission = true
            state.value = MainViewState.RefreshLocationState(cityWeather)
        } else {
            state.value = MainViewState.NeedsPermissionState
        }
    }

    fun onUserGrantedPermission() {
        hasPermission = true
        state.value = MainViewState.RefreshLocationState(cityWeather)
    }

    fun onUserWantsToRefresh() {
        state.value = state.value
    }

    fun onNewLocationRetrieved(location: Location) {
        weatherRepository.getWeatherForCurrentLocation(location) {setupData(it)}
    }

    private fun setupData(cityWeather: CityWeather) {
        this. cityWeather = cityWeather
        state.value = MainViewState.DisplayLocationState(cityWeather)
    }

}