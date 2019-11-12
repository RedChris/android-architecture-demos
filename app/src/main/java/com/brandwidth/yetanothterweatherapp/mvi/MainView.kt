package com.brandwidth.yetanothterweatherapp.mvi

import io.reactivex.Observable


interface MainView : MviView {

    fun pullToRefreshIntent(): Observable<MainAction.PullToRefresh>
    fun currentPermissionStatusIntent(): Observable<MainAction.PermissionStatus>
    fun permissionChangedIntent(): Observable<MainAction.PermissionChanged>
    fun newLocationIntent(): Observable<MainAction.NewLocation>

    fun render(state: MainViewState)

}

interface MainFragmentPresenter {

    fun attachView(view: MainView)
    fun detachView()

}