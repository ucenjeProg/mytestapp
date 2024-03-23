package com.mycustomappapply.wotttoo.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mycustomappapply.wotttoo.data.repositories.MainRepository
import com.mycustomappapply.wotttoo.models.BasicResponse
import com.mycustomappapply.wotttoo.models.Notification
import com.mycustomappapply.wotttoo.models.NotificationsResponse
import com.mycustomappapply.wotttoo.utils.Constants.CODE_AUTHENTICATION_FAIL
import com.mycustomappapply.wotttoo.utils.Constants.CODE_SERVER_ERROR
import com.mycustomappapply.wotttoo.utils.Constants.CODE_SUCCESS
import com.mycustomappapply.wotttoo.utils.Constants.CODE_VALIDATION_FAIL
import com.mycustomappapply.wotttoo.utils.DataState
import com.mycustomappapply.wotttoo.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    application: Application,
    val mainRepository: MainRepository,
) : AndroidViewModel(application) {

    private val notificationList: MutableList<Notification> = mutableListOf<Notification>()

    private val _notifications = MutableLiveData<DataState<NotificationsResponse>>()
    val notifications: MutableLiveData<DataState<NotificationsResponse>>
        get() = _notifications

    private val _clearNotifications = MutableLiveData<DataState<NotificationsResponse>>()
    val clearNotifications: MutableLiveData<DataState<NotificationsResponse>>
        get() = _clearNotifications


    fun getNotifications(
        page: Int = 1,
        forced: Boolean = false
    ): Job = viewModelScope.launch(Dispatchers.IO) {
            try {
                if (Utils.hasInternetConnection(getApplication<Application>())) {
                    if (_notifications.value == null || forced) {
                        _notifications.postValue(DataState.Loading())
                        val response: Response<NotificationsResponse> = mainRepository.getNotifications(page)
                        val handledResponse: DataState<NotificationsResponse> = handleNotificationsResponse(response, page)
                        _notifications.postValue(handledResponse)
                    }
                } else {
                    _notifications.postValue(DataState.Fail(message = "No internet connection"))
                }
            } catch (e: Exception) {
                _notifications.postValue(DataState.Fail())
            }
        }


    fun clearNotifications(): Job = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (Utils.hasInternetConnection(getApplication<Application>())) {
                _clearNotifications.postValue(DataState.Loading())
                val response: Response<NotificationsResponse> = mainRepository.clearNotifications()
                val handledResponse: DataState<NotificationsResponse> = handleNotificationsResponse(response, page = 1)
                _clearNotifications.postValue(handledResponse)
            } else {
                _clearNotifications.postValue(DataState.Fail(message = "No internet connection"))
            }
        } catch (e: Exception) {
            _clearNotifications.postValue(DataState.Fail())
        }
    }

    private fun handleNotificationsResponse(
        response: Response<NotificationsResponse>,
        page: Int
    ): DataState<NotificationsResponse> {
        when (response.code()) {
            CODE_SUCCESS -> {
                if (page == 1) {
                    notificationList.clear()
                }
                response.body()?.notifications?.forEach { notification ->
                    notificationList.add(notification)
                }

                return DataState.Success(
                    data = NotificationsResponse(
                        notifications = notificationList
                    )
                )
            }

            CODE_AUTHENTICATION_FAIL -> {
                val authResponse: BasicResponse = Utils.toBasicResponse(response)
                return DataState.Fail(message = authResponse.message)
            }

            CODE_SERVER_ERROR -> {
                val serverResponse: BasicResponse = Utils.toBasicResponse(response)
                return DataState.Fail(message = serverResponse.message)
            }

            CODE_VALIDATION_FAIL -> {
                val validationResponse: BasicResponse = Utils.toBasicResponse(response)
                return DataState.Fail(message = validationResponse.message)
            }

            else -> {
                return DataState.Fail()
            }
        }
    }


}