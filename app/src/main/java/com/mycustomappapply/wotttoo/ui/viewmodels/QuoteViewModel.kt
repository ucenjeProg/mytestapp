package com.mycustomappapply.wotttoo.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mycustomappapply.wotttoo.data.repositories.MainRepository
import com.mycustomappapply.wotttoo.models.ArticleResponse
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.PostArticleResponse
import com.mycustomappapply.wotttoo.models.Quote
import com.mycustomappapply.wotttoo.utils.Constants
import com.mycustomappapply.wotttoo.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    application: Application,
) : AndroidViewModel(application) {

    private var quoteList: MutableList<Quote> = mutableListOf<Quote>()
    private val _quotes = MutableLiveData<DataState<ArticleResponse>>()
    val quotes: LiveData<DataState<ArticleResponse>>
        get() = _quotes

    private val _quotesByGenre = MutableLiveData<DataState<ArticleResponse>>()
    val quotesByGenre: LiveData<DataState<ArticleResponse>>
        get() = _quotesByGenre

    private val _quote = MutableLiveData<DataState<ArticleResponse>>()
    val quote: LiveData<DataState<ArticleResponse>>
        get() = _quote

    private val _likeQuote = MutableLiveData<DataState<ArticleResponse>>()
    val likeQuote: LiveData<DataState<ArticleResponse>>
        get() = _likeQuote

    private val _updateQuote = MutableLiveData<DataState<ArticleResponse>>()
    val updateQuote: LiveData<DataState<ArticleResponse>>
        get() = _updateQuote

    private val _deleteQuote = MutableLiveData<DataState<ArticleResponse>>()
    val deleteQuote: LiveData<DataState<ArticleResponse>>
        get() = _deleteQuote


    fun getQuotesByGenre(
        genre: String,
        page: Int = 1,
        forced: Boolean = false
    ): Job =
        viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    if (_quotes.value == null || page > 1 || forced) {
                        _quotes.postValue(DataState.Loading())
                        val response: Response<ArticleResponse> = mainRepository.getQuotesByGenre(genre, page)
                        handleQuoteResponse(response, forced)
                    }
                } catch (e: Exception) {
                    _quotes.postValue(DataState.Fail())
                }
            } else {
                _quotes.postValue(DataState.Fail(message = "No internet connection"))
            }
        }


    fun getQuotes(
        page: Int = 1,
        forced: Boolean = false
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                if (_quotes.value == null || forced) {
                    _quotes.postValue(DataState.Loading())
                    val response: Response<ArticleResponse> = mainRepository.getQuotes(page)
                    handleQuoteResponse(response, forced)
                }
            } catch (exception: Exception) {
                Log.d("mytag", "getQuotes: ${exception.message}")
                _quotes.postValue(DataState.Fail())
            }
        } else {
            _quotes.postValue(DataState.Fail(message = "No internet connection"))
        }

    }

    fun getSingleQuote(
        quoteId: String
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                _quote.postValue(DataState.Loading())
                val response: Response<ArticleResponse> = mainRepository.getSingleQuote(quoteId)
                val handledResponse: DataState<ArticleResponse> = handleQuoteResponse(response)
                _quote.postValue(handledResponse)
            } catch (exception: Exception) {
                _quote.postValue(DataState.Fail())
            }
        } else {
            _quote.postValue(DataState.Fail(message = "No internet connection"))
        }

    }

    fun updateQuote(

        oldQuote: Quote,
        newQuote: Map<String, String>
    ): Job =
        viewModelScope.launch(Dispatchers.IO) {
            if (hasInternetConnection()) {
                try {
                    _updateQuote.postValue(DataState.Loading())
                    val response: Response<ArticleResponse> = mainRepository.updateQuote(
                        quoteId = oldQuote.id,
                        quote = newQuote
                    )
                    val handledResponse: DataState<ArticleResponse> = handleQuoteResponse(response)
                    val index: Int = quoteList.indexOf(oldQuote)
                    if (index != -1) {
                        /*   quoteList.removeAt(index)
                           val newQuoteModel: Quote =
                               oldQuote.copy(quote = newQuote["quote"], genre = newQuote["genre"])
                           quoteList.add(index, newQuoteModel)*/
                    }
                    //  _quotes.postValue(DataState.Success(QuoteResponse(quoteList.toList())))
                    _updateQuote.postValue(handledResponse)
                } catch (e: Exception) {
                    _updateQuote.postValue(DataState.Fail())
                }
            } else {
                _updateQuote.postValue(DataState.Fail(message = "No internet connection"))
            }

        }

    fun deleteQuote(
        quote: Quote
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                _deleteQuote.postValue(DataState.Loading())
                val response: Response<ArticleResponse> = mainRepository.deleteQuote(quote.id)
                val handledResponse: DataState<ArticleResponse> = handleQuoteResponse(response)

                if (handledResponse is DataState.Success) {
                    quoteList.remove(quote)
                    _quotes.postValue(DataState.Success(data = ArticleResponse(quoteList)))
                }
                _deleteQuote.postValue(handledResponse)
            } catch (e: Exception) {
                _deleteQuote.postValue(DataState.Fail())
            }
        } else {
            _deleteQuote.postValue(DataState.Fail(message = "No internet connection"))
        }
    }


    fun getMoreQuotes(page: Int = 1): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                _quotes.postValue(DataState.Loading())
                val response: Response<ArticleResponse> = mainRepository.getQuotes(page)
                handleQuoteResponse(response)
            } catch (e: Exception) {

                _quotes.postValue(DataState.Fail())
            }
        } else {
            _quotes.postValue(DataState.Fail(message = "No internet connection"))
        }
    }

    fun postQuote(
        body: Map<String, String>
    ): Job = viewModelScope.launch(Dispatchers.IO) {

        if (hasInternetConnection()) {

            try {
                _quote.postValue(DataState.Loading())

                val response: Response<PostArticleResponse> = mainRepository.postQuote(body)
                when (response.code()) {
                    Constants.CODE_SUCCESS -> {
                        _quote.postValue(DataState.Success())
                    }

                    Constants.CODE_CREATION_SUCCESS -> {
                        _quote.postValue(DataState.Success())
                    }

                    Constants.CODE_VALIDATION_FAIL -> {
                        val authFailResponse: BasicResponse = Gson().fromJson(
                            response.errorBody()?.charStream(),
                            BasicResponse::class.java
                        )
                        _quote.postValue(DataState.Fail(message = authFailResponse.message))
                    }

                    Constants.CODE_SERVER_ERROR -> {
                        _quote.postValue(DataState.Fail(message = "Server error"))
                    }

                    Constants.CODE_AUTHENTICATION_FAIL -> {
                        val authFailResponse: BasicResponse = Gson().fromJson(
                            response.errorBody()?.charStream(),
                            BasicResponse::class.java
                        )
                        _quote.postValue(DataState.Fail(message = authFailResponse.message))
                    }

                    else -> {
                        _quote.postValue(DataState.Fail(message = "No error code provided"))
                    }
                }

            } catch (e: Exception) {
                _quote.postValue(DataState.Fail())
            }
        } else {
            _quote.postValue(DataState.Fail(message = "No internet connection"))
        }
    }

    fun toggleLike(
        quote: Quote
    ): Job = viewModelScope.launch(Dispatchers.IO) {
        if (hasInternetConnection()) {
            try {
                _likeQuote.postValue(DataState.Loading())
                _quotes.value?.data?.data?.find { q: Quote -> q.id == quote.id }?.let { item: Quote ->
                    item.attributes.likes = quote.attributes.likes
                    item.attributes.liked = quote.attributes.liked
                }
                val response: Response<ArticleResponse> = mainRepository.likeOrDislikeQuote(quote.id)
                val handledResponse: DataState<ArticleResponse> = handleQuoteResponse(response)
                _likeQuote.postValue(handledResponse)
            } catch (e: Exception) {
                _likeQuote.postValue(DataState.Fail())
            }
        } else {
            _likeQuote.postValue(DataState.Fail(message = "No internet connection"))
        }

    }

    private fun handleQuoteResponse(
        response: Response<ArticleResponse>,
        forced: Boolean = false
    ) {
        when (response.code()) {
            Constants.CODE_SUCCESS -> {
                // Check when there is not any quote
                if (quoteList.isEmpty() || forced) {
                    quoteList = response.body()!!.data.toMutableList()
                } else {
                    response.body()!!.data.forEach { quote ->
                        quoteList.add(quote)
                    }
                }
                _quotes.postValue(DataState.Success(ArticleResponse(data = quoteList)))

            }

            Constants.CODE_SERVER_ERROR -> {
                _quotes.postValue(DataState.Fail(message = "Server error"))
            }

            Constants.CODE_AUTHENTICATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                _quotes.postValue(DataState.Fail(message = authFailResponse.message))
            }
        }
    }


    private fun handleQuoteResponse(response: Response<ArticleResponse>): DataState<ArticleResponse> {
        when (response.code()) {
            Constants.CODE_SUCCESS -> {
                return DataState.Success(response.body())
            }

            Constants.CODE_CREATION_SUCCESS -> {
                return DataState.Success(response.body())
            }

            Constants.CODE_VALIDATION_FAIL -> {
                val authFailResponse: BasicResponse = Gson().fromJson(
                    response.errorBody()?.charStream(),
                    BasicResponse::class.java
                )
                return DataState.Fail(message = authFailResponse.message)
            }

            Constants.CODE_SERVER_ERROR -> {
                return DataState.Fail(message = "Server error")
            }

            Constants.CODE_AUTHENTICATION_FAIL -> {
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

    fun clearLiveDataValues() {
        _deleteQuote.value = null
        _updateQuote.value = null
        _quote.value = null
    }
}