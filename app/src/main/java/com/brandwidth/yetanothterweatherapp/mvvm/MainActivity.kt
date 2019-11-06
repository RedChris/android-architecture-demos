package com.brandwidth.yetanothterweatherapp.mvvm

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.brandwidth.yetanothterweatherapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val LOCATION_CODE_REQUEST = 200
    private lateinit var mainViewModel : MainViewModel


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        swiperefresh.setOnRefreshListener { mainViewModel.onUserWantsToRefresh()}

        mainViewModel.checkForLocationPermissionEvent.observe(this, Observer<Unit> { mainViewModel.onHasLocationPermission(checkForLocationPermission()) })
        mainViewModel.requestLocationPermissionEvent.observe(this, Observer<Unit> { requestLocationPermission() })
        mainViewModel.requestLastKnownLocationEvent.observe(this, Observer<Unit> { requestLastKnownLocation() })
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                mainViewModel.onNewLocationRetrieved(location!!)
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_CODE_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mainViewModel.onUserGrantedPermission()
                }
                return
            }
        }
    }

    private fun checkForLocationPermission()
            = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestLastKnownLocation() {
        getLocation()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_CODE_REQUEST)
    }

}
