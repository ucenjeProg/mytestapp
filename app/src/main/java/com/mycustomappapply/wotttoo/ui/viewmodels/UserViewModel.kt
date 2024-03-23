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
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.CurrentUSerResponse
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.models.User
import com.mycustomappapply.wotttoo.models.UsersResponse
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
class UserViewModel @Inject constructor(
    val shrdPrefMngr: SharedPreferencesRepository,
    val userRepository: UserRepository,
    application: Application,
) : AndroidViewModel(application) {

    private var userQuoteList: MutableList<Quote> = mutableListOf<Quote>()
    private var usersList: MutableList<User> = mutableListOf<User>()

    private val _user = MutableLiveData<DataState<CurrentUSerResponse>>()
    val user: LiveData<DataState<CurrentUSerResponse>>
        get() = _user

    private val _userQuotes = MutableLiveData<DataState<ArticleResponse>>()
    val userQuotes: LiveData<DataState<ArticleResponse>>
        get() = _userQuotes

    private val _users = MutableLiveData<DataState<UsersResponse>>()
    val users: LiveData<DataState<UsersResponse>>
        get() = _users


    fun getMoreUserQuotes(
        userId: String? = null,
        page: Int
    ): Job = viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    _userQuotes.postValue(DataState.Loading())
                    val id: String = userId ?: shrdPrefMngr.getCurrentUser()?.userId!!
                    val response: Response<ArticleResponse> = userRepository.getMoreUserQuotes(id, page)
                    handleQuoteResponse(response)
                } catch (e: Exception) {
                    _userQuotes.postValue(DataState.Fail())
                }
            } else {
                _userQuotes.postValue(DataState.Fail(message = "No internet connection"))
            }

        }

    fun getUser(
        userId: String? = null,
        forced: Boolean = false
    ): Job = viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    if (_user.value == null || forced) {
                        _user.postValue(DataState.Loading())
                        val response: Response<CurrentUSerResponse> = userRepository.getUser("296")
                        val handledResponse: DataState<CurrentUSerResponse> = handleUsersResponse(response)
                        _user.postValue(handledResponse)
                    }
                } catch (e: Exception) {
                    _user.postValue(DataState.Fail())
                }
            } else {
                _user.postValue(DataState.Fail(message = "No internet connection"))
            }

        }

    fun notifyQuoteRemoved(
        quote: Quote
    ) {
        userQuoteList.remove(quote)
    }

    fun notifyQuoteUpdated(
        quote: Quote
    ): Job = viewModelScope.launch(Dispatchers.Default) {
        if (hasInternetConnection()) {
            try {
                val quoteToDelete: Quote? = userQuoteList.find { q -> q.id == quote.id }
                val index: Int = userQuoteList.indexOf(quoteToDelete)
                if (index != -1) {
                    userQuoteList.remove(quoteToDelete)
                    userQuoteList.add(index, quote)
                }
            } catch (e: Exception) {
                _userQuotes.postValue(DataState.Fail())
            }
        } else {
            _userQuotes.postValue(DataState.Fail(message = "No internet connection"))
        }

    }

    fun getUsers(
        searchQuery: String = "",
        paginating: Boolean = false,
        currentPage: Int = 1
    ): Job = viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    _users.postValue(DataState.Loading())
                    val response: Response<UsersResponse> = userRepository.getUsers()

                    when (response.code()) {

                        CODE_SUCCESS -> {
                            if (paginating) {
                                response.body()?.data?.forEach { user: User ->
                                    usersList.add(user)
                                }
                            } else {
                                usersList = response.body()?.data?.toMutableList() ?: mutableListOf()
                            }
                            _users.postValue(
                                DataState.Success(
                                    UsersResponse(
                                        data = usersList
                                    )
                                )
                            )
                        }

                        CODE_SERVER_ERROR -> {
                            _users.postValue(DataState.Fail(message = "Server error"))
                        }
                    }


                } catch (e: Exception) {
                    _users.postValue(DataState.Fail())
                }
            } else {
                _users.postValue(DataState.Fail(message = "No internet connection"))
            }

        }


    fun updateUser(
        username: String,
        fullname: String,
        bio: String
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (hasInternetConnection()) {

                val body: Map<String, String> = mapOf(
                    "username" to username,
                    "fullname" to fullname,
                    "bio" to bio
                )

                userRepository.updateUser(body)
                val response: Response<CurrentUSerResponse> = userRepository.updateUser(body)
                val handledResponse: DataState<CurrentUSerResponse> = handleUsersResponse(response, true)
                _user.postValue(handledResponse)

            } else {
                _user.postValue(DataState.Fail(message = "No internet connection"))
            }
        } catch (e: Exception) {
            _user.postValue(DataState.Fail(message = "Something went wrong: ${e.cause}"))
        }
    }

    private fun handleQuoteResponse(
        response: Response<ArticleResponse>
    ) {

        when (response.code()) {

            CODE_SUCCESS -> {
                response.body()?.data?.forEach { quote: Quote ->
                    userQuoteList.add(quote)
                }
                _userQuotes.postValue(DataState.Success(ArticleResponse(userQuoteList)))
            }

            CODE_CREATION_SUCCESS -> {

            }

            CODE_VALIDATION_FAIL -> {

            }

            CODE_SERVER_ERROR -> {
                _userQuotes.postValue(DataState.Fail(message = "Server error"))
            }

            CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _userQuotes.postValue(DataState.Fail(message = authFailResponse.message))
            }
        }
    }


    private fun handleUsersResponse(
        response: Response<CurrentUSerResponse>,
        userUpdated: Boolean = false
    ): DataState<CurrentUSerResponse> {
        when (response.code()) {

            CODE_SUCCESS -> {
                return DataState.Success(response.body())
            }

            CODE_CREATION_SUCCESS -> {
                return DataState.Success(response.body())
            }

            CODE_VALIDATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                return DataState.Fail(message = authFailResponse.message)
            }

            CODE_SERVER_ERROR -> {
                return DataState.Fail(message = "Server error")
            }

            CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                return DataState.Fail(message = authFailResponse.message)
            }
        }
        return DataState.Fail(message = "No error code provided")
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