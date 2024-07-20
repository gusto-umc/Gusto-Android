package com.gst.gusto.my.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.MyPublishData
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.UsersRepositoryImpl
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class MySettingViewModel(
    private val usersRepository: UsersRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
) : ViewModel() {

    private val xAuthToken = GustoApplication.prefs.getSharedPrefs().first
    private val refreshToken = GustoApplication.prefs.getSharedPrefs().second

    private val _myPublishData = MutableLiveData<MyPublishData?>()
    val myPublishData : LiveData<MyPublishData?> = _myPublishData

    private val _errorToastData: MutableLiveData<Unit> = MutableLiveData()
    val errorToastData: LiveData<Unit> = _errorToastData

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData


    init {
        getMyPublish()
    }

    fun getMyPublish() = viewModelScope.launch {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when(val response = usersRepository.getMyPublish(token)){
            is ApiResponse.Success -> {
                _myPublishData.value = response.data
            }
            is ApiResponse.Error -> {
                if(response.errorCode == 403){
                    _tokenToastData.value = Unit
                    setRefreshToken()
                } else {
                    _errorToastData.value = Unit
                }
            }
        }
    }

    suspend fun setRefreshToken() = viewModelScope.launch {
        while (true) {
            when (val response = authRepository.getRefreshToken(xAuthToken, refreshToken)) {
                is ApiResponse.Success -> {
                    GustoApplication.prefs.setSharedPrefs(
                        response.data.xAuthToken,
                        response.data.refreshToken
                    )
                    break
                }
                is ApiResponse.Error -> {
                    if (response.errorCode != 403) {
                        break
                    }
                }
            }
        }
    }
}