package com.brandwidth.yetanothterweatherapp.mvi

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brandwidth.yetanothterweatherapp.R
import com.brandwidth.yetanothterweatherapp.data.CityWeather
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject


class MainActivity : AppCompatActivity(), MainView {

    private val permsissonCurrentlyGrantedSource = PublishSubject.create<Boolean>()
    private val permsissonSource = AsyncSubject.create<Boolean>()
    private val locationSource = BehaviorSubject.create<Location>()

    private val LOCATION_CODE_REQUEST = 200
    private val mainPresenter = MainPresenter(
        WeatherRespository(), TemperatureConverter())


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainPresenter.attachView(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.detachView()
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                locationSource.onNext(location!!)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_CODE_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permsissonSource.onNext(true)
                    permsissonSource.onComplete()
                }
                return
            }
        }
    }

    override fun pullToRefreshIntent(): Observable<MainAction.PullToRefresh>
        = swiperefresh.refreshes().map { MainAction.PullToRefresh }

    override fun permissionChangedIntent(): Observable<MainAction.PermissionChanged>
        = permsissonSource.map { MainAction.PermissionChanged }

    override fun currentPermissionStatusIntent(): Observable<MainAction.PermissionStatus>
        = permsissonCurrentlyGrantedSource.map { MainAction.PermissionStatus(it) }

    override fun newLocationIntent(): Observable<MainAction.NewLocation>
        = locationSource.map { MainAction.NewLocation(location = it) }

    override fun render(state: MainViewState) {
        swiperefresh.isRefreshing = state.pullToRefreshing

        when {
            state.cityWeatherData != null -> {
                setData(cityWeatherData = state.cityWeatherData)
            }

            state.requestLocation -> {
                requestLatKnownLocation()
            }

            state.checkForPermissionGranted -> {
                checkForLocationPermission()
            }

            state.requestPermission -> {
                requestLocationPermission()
            }
        }
    }

    private fun checkForLocationPermission() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        permsissonCurrentlyGrantedSource.onNext(permissionGranted)
    }

    private fun requestLatKnownLocation() {
        getLocation()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_CODE_REQUEST)
    }

    private fun setData(cityWeatherData: CityWeatherData) {
        locationName.text = cityWeatherData.cityWeather.name
        weather.text = cityWeatherData.cityWeather.weather.main
        weatherDescription.text = cityWeatherData.cityWeather.weather.description
        windSpeed.text = cityWeatherData.cityWeather.wind.speed.toString()
        tempreture.text = cityWeatherData.tempreture
    }

}
