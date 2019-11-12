package com.brandwidth.yetanothterweatherapp.mvi

import android.util.Log
import com.brandwidth.yetanothterweatherapp.domain.TemperatureConverter
import com.brandwidth.yetanothterweatherapp.data.WeatherRespository
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainPresenter (private val weatherRespository: WeatherRespository,
                     private val tempretureConverter: TemperatureConverter): MainFragmentPresenter {


    private var hasPermission = false

    var disposable: Disposable? = null
    var view: MainView? = null

    override fun attachView(view: MainView) {
        this.view = view

        val permissionChangedResult = permissionChangedResult(view)
        val pullToRefreshResult = pullToRefreshResult(view)
        val permissionStatusResult = permissionStatusResult(view)
        val requestLocationResult = requestLocationResult(view)

        val allIntentObservable = Observable.merge(
            permissionChangedResult,
            pullToRefreshResult,
            permissionStatusResult,
            requestLocationResult)

        val initialState = MainViewState.init()

        disposable = allIntentObservable
            .scan(initialState) { state, result -> state.reduce(result)}
            .subscribe (
                { state ->
                    Log.d("Got state ","$state")
                    view.render(state) },
                {e -> Log.d("state",e.toString())})


    }


    private fun pullToRefreshResult(view: MainView): Observable<MainResult>? {
        return view.pullToRefreshIntent()
            .map { MainResult.RequestLocation }
    }

    private fun permissionChangedResult(view: MainView): Observable<MainResult>? {
        return view.permissionChangedIntent()
            .map {
                hasPermission = true
                MainResult.RequestLocation
            }
    }

    private fun permissionStatusResult(view: MainView): Observable<MainResult>? {
        return view.currentPermissionStatusIntent()
            .map {
                hasPermission = it.permissionGranted

                return@map if (it.permissionGranted)
                    MainResult.RequestLocation
                else
                    MainResult.RequestPermission
            }
    }

    private fun requestLocationResult(view: MainView): Observable<MainResult>? {
        return view.newLocationIntent()
            .observeOn(Schedulers.io())
            .flatMap { action ->
                weatherRespository.getWeatherForCurrentLocation(action.location)
                .map { weather ->
                    val temp = tempretureConverter.convertKelvinToCelcius(weather.main.temp).toString()
                    CityWeatherData(cityWeather = weather, tempreture = temp) }
                    .map { MainResult.PullToRefreshComplete(it) }

            }
    }

    override fun detachView() {
        disposable?.dispose()
        disposable = null
        view = null
    }


}