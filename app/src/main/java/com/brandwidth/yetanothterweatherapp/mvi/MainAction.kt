package com.brandwidth.yetanothterweatherapp.mvi

import android.location.Location


data class MainViewState(

    val checkForPermissionGranted: Boolean,
    val requestPermission: Boolean,
    val requestLocation: Boolean,
    val cityWeatherData: CityWeatherData?,
    val pullToRefreshing: Boolean) {

    companion object Factory {
        fun init() = MainViewState (
            checkForPermissionGranted = true,
            requestPermission = false,
            requestLocation = false,
            cityWeatherData = null,
            pullToRefreshing = false
        )
    }

    fun reduce(result: MainResult): MainViewState {

        return when (result) {
            is MainResult.CheckForPermission -> copy(
                checkForPermissionGranted = true,
                requestPermission = false)

            is MainResult.RequestPermission -> copy(
                checkForPermissionGranted = false,
                requestPermission = true)

            is MainResult.RequestLocation -> copy(
                requestLocation = true)

            is MainResult.PullToRefreshing -> copy(
                pullToRefreshing = true)

            is MainResult.PullToRefreshComplete -> copy(
                pullToRefreshing = false,
                cityWeatherData = result.cityWeatherData)

        }
    }
}

sealed class MainAction {
    object PullToRefresh: MainAction()
    object PermissionChanged: MainAction()
    data class PermissionStatus(val permissionGranted: Boolean): MainAction()
    data class NewLocation(val location: Location): MainAction()
}
sealed class MainResult {

    object CheckForPermission: MainResult()
    object RequestPermission: MainResult()
    object RequestLocation: MainResult()
    object PullToRefreshing: MainResult()
    data class PullToRefreshComplete(val cityWeatherData: CityWeatherData?): MainResult()

}