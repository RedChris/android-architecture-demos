package com.brandwidth.yetanothterweatherapp.mvp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.brandwidth.yetanothterweatherapp.R
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {

    private val LOCATION_CODE_REQUEST = 200
    private val mainPresenter = MainPresenter(this,
        WeatherRespository(), TemperatureConverter())


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        swiperefresh.setOnRefreshListener { mainPresenter.onUserWantsToRefresh()}
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                mainPresenter.onNewLocationRetrieved(location!!)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_CODE_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mainPresenter.onUserGrantedPermission()
                }
                return
            }
        }
    }

    override fun checkForLocationPermission()
            = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun requestLatKnownLocation() {
        getLocation()
    }

    override fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_CODE_REQUEST)
    }

    override fun setLocationName(name: String) {
        locationName.text = name
    }

    override fun setWeatherTitle(title: String) {
        weather.text = title
    }

    override fun setWeatherDescription(description: String) {
        weatherDescription.text = description
    }

    override fun setWindSpeed(speed: String) {
        windSpeed.text  = speed
    }

    override fun setTemperature(temperature: String) {
        tempreture.text = temperature
    }

}
