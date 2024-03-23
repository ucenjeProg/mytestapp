package com.mycustomappapply.wotttoo.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.mycustomappapply.wotttoo.models.BasicResponse
import retrofit2.Response

object Utils {
    fun <T> toBasicResponse(response: Response<T>): BasicResponse {
        return Gson().fromJson(
            response.errorBody()?.charStream(),
            BasicResponse::class.java
        )
    }

    fun hasInternetConnection(
        application: Application
    ): Boolean {
        val connectivityManager: ConnectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork: Network = connectivityManager.activeNetwork ?: return false
        val capabilities: NetworkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun showKeyboard(
        view: View
    ) {
        val inputManager: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(
        view: View
    ) {
        val inputManager: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}