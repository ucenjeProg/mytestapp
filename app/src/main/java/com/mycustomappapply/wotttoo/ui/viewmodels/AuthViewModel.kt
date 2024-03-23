package com.mycustomappapply.wotttoo.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mycustomappapply.wotttoo.data.local.SharedPreferencesRepository
import com.mycustomappapply.wotttoo.data.repositories.UserRepository
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.UserAuth
import com.mycustomappapply.wotttoo.utils.Constants.CODE_AUTHENTICATION_FAIL
import com.mycustomappapply.wotttoo.utils.Constants.CODE_CREATION_SUCCESS
import com.mycustomappapply.wotttoo.utils.Constants.CODE_SERVER_ERROR
import com.mycustomappapply.wotttoo.utils.Constants.CODE_SUCCESS
import com.mycustomappapply.wotttoo.utils.Constants.CODE_VALIDATION_FAIL
import com.mycustomappapply.wotttoo.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    val shrdPrefMngr: SharedPreferencesRepository,
    val userRepository: UserRepository,
) : AndroidViewModel(application) {

    var currentUser: UserAuth? = null

    private var _isAuthenticated = false
    val isAuthenticated
        get() = _isAuthenticated

    private var _currentUserId: String? = null
    val currentUserId
        get() = _currentUserId

    private val _auth = MutableLiveData<DataState<UserAuth>>()
    val auth: LiveData<DataState<UserAuth>>
        get() = _auth

    private val _genres = MutableLiveData<DataState<UserAuth>>()
    val genres: LiveData<DataState<UserAuth>>
        get() = _genres

    init {
        initAuthentication()
    }

    private fun initAuthentication() {
        val user: UserAuth = shrdPrefMngr.getCurrentUser()
        _currentUserId = user.userId
        _isAuthenticated = user.userId != null && user.token != null
        currentUser = user
    }

    fun getFollowingGenres(): String = shrdPrefMngr.getFollowingGenres()

    fun logout(): Unit = shrdPrefMngr.clearUser()

    fun authorizeWithGoogle(
        email: String,
        fullname: String,
        username: String,
        profileImage: String
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                _auth.postValue(DataState.Loading())
                val response: Response<UserAuth> =
                    userRepository.authorizeWithGoogle(email, fullname, username, profileImage)
                handleResponse(response)
            } catch (e: Exception) {
                _auth.postValue(DataState.Fail())
            }
        } else {
            _auth.postValue(DataState.Fail(message = "No internet connection"))
        }

    }

    fun getUser(): UserAuth = shrdPrefMngr.getCurrentUser()

    fun saveUser() {
        if (auth.value?.data != null) {
            shrdPrefMngr.saveUser(auth.value?.data)
        }
    }


    private fun handleResponse(
        response: Response<UserAuth>
    ) {
        when (response.code()) {
            CODE_SUCCESS -> {
                _auth.postValue(DataState.Success(response.body()))
            }

            CODE_CREATION_SUCCESS -> {
                _auth.postValue(DataState.Success(response.body()))
            }

            CODE_VALIDATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _auth.postValue(DataState.Fail(message = authFailResponse.message))
            }

            CODE_SERVER_ERROR -> {
                _auth.postValue(DataState.Fail(message = "Server error"))
            }

            CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _auth.postValue(DataState.Fail(message = authFailResponse.message))
            }
        }
    }

    fun saveFollowingGenres(
        genres: String,
        userId: String? = null
    ) =
        viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    _genres.postValue(DataState.Loading())
                    shrdPrefMngr.saveFollowingGenres("time")
                    if (userId == null) {
                        _genres.postValue(DataState.Success())
                    } else {
                        val response: Response<UserAuth> = userRepository.saveFollowingGenres(genres)
                        when (response.code()) {
                            CODE_SERVER_ERROR -> {
                                _genres.postValue(DataState.Fail(message = "Something went wrong in server"))
                            }

                            CODE_AUTHENTICATION_FAIL -> {
                                _genres.postValue(DataState.Fail(message = "You are not authenticated"))
                            }

                            CODE_SUCCESS -> {
                                _genres.postValue(DataState.Success(response.body()))
                            }

                        }
                    }
                } catch (e: Exception) {
                    _genres.postValue(DataState.Fail(message = "Something went wrong: ${e.message}"))
                }
            } else {
                _genres.postValue(DataState.Fail(message = "No internet connection"))
            }

        }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager: ConnectivityManager = getApplication<Application>().getSystemService(
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
}