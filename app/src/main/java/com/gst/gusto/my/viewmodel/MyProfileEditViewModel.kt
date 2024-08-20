package com.gst.gusto.my.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gst.gusto.R
import com.gst.gusto.api.ApiResponse
import com.gst.gusto.model.MyProfileData
import com.gst.gusto.my.Gender
import com.gst.gusto.my.toAge
import com.gst.gusto.my.toGender
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.UsersRepositoryImpl
import com.gst.gusto.util.GustoApplication
import kotlinx.coroutines.launch

class MyProfileEditViewModel(
    private val usersRepository: UsersRepositoryImpl,
    private val authRepository: AuthRepositoryImpl
): ViewModel() {
    private val xAuthToken = GustoApplication.prefs.getSharedPrefs().first
    private val refreshToken = GustoApplication.prefs.getSharedPrefs().second

    private val _myProfileData = MutableLiveData<MyProfileData?>()
    val myProfileData: LiveData<MyProfileData?> = _myProfileData

    private val _errorToastData: MutableLiveData<Unit> = MutableLiveData()
    val errorToastData: LiveData<Unit> = _errorToastData

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit> = _tokenToastData

    private val _checkNickNameData: MutableLiveData<Boolean> = MutableLiveData()
    val checkNickNameData :LiveData<Boolean> = _checkNickNameData

    private val _modifyNickNameData: MutableLiveData<Boolean> = MutableLiveData()
    val modifyNickNameData :LiveData<Boolean> = _modifyNickNameData

    private val _modifyProfileImgData: MutableLiveData<Boolean> = MutableLiveData()
    val modifyProfileImgData :LiveData<Boolean> = _modifyProfileImgData

    private val _setProfileData: MutableLiveData<Boolean> = MutableLiveData()
    val setProfileData :LiveData<Boolean> = _setProfileData

    init {
        getMyProfile()
        _modifyNickNameData.value = false
        _modifyProfileImgData.value = false
    }

    fun getMyProfile() = viewModelScope.launch {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when(val response = usersRepository.getMyProfile(token)){
            is ApiResponse.Success -> {
                _myProfileData.value = response.data
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

    fun getCheckNickname(nickname: String) = viewModelScope.launch {
        val token = GustoApplication.prefs.getSharedPrefs().first
        when(val response = usersRepository.getCheckNickname(token, nickname)){
            is ApiResponse.Success -> {
                _checkNickNameData.value = true
                _modifyNickNameData.value = true
            }
            is ApiResponse.Error -> {
                if(response.errorCode == 403){
                    _tokenToastData.value = Unit
                    setRefreshToken()
                } else if(response.errorCode == 409) {
                    _checkNickNameData.value = false
                } else {
                    _errorToastData.value = Unit
                }
            }
        }
    }

    fun setGender(genderText: String) {

        val gender = toGender(genderText)

        _myProfileData.value?.let {
            val updatedProfileData = it.copy(gender = gender?.name.toString())
            _myProfileData.value = updatedProfileData
        }
    }

    fun setAge(ageText: String) {

        val age = toAge(ageText)

        _myProfileData.value?.let {
            val updatedProfileData = it.copy(age = age?.name.toString())
            _myProfileData.value = updatedProfileData
        }
    }

    fun setNickname(nickname: String) {
        _myProfileData.value?.let {
            val updatedProfileData = it.copy(nickname = nickname)
            _myProfileData.value = updatedProfileData
        }
    }

    fun setDefaultProfileImg(){
        _modifyProfileImgData.value = true
        _myProfileData.value?.let {
            val updatedProfileData = it.copy(profileImg = null)
            _myProfileData.value = updatedProfileData
        }
    }

    fun setProfileImg(profileImg: String) = viewModelScope.launch {
        _modifyProfileImgData.value = true
        _myProfileData.value?.let {
            val updatedProfileData = it.copy(profileImg = profileImg)
            _myProfileData.value = updatedProfileData
        }

    }

    fun setProfile() = viewModelScope.launch {
        var isAllSuccessful = true

        // 1. 프로필 정보 업데이트
        if (modifyNickNameData.value == false) {
            _myProfileData.value?.let {
                val updatedProfileData = it.copy(nickname = null)
                _myProfileData.value = updatedProfileData
            }
        }

        val token = GustoApplication.prefs.getSharedPrefs().first

        // 프로필 정보 업데이트
        val profileResponse = myProfileData.value?.let { usersRepository.setMyProfileInfo(token, it) }
        when (profileResponse) {
            is ApiResponse.Success -> { /* nothing to do here */ }
            is ApiResponse.Error -> {
                isAllSuccessful = false
                if (profileResponse.errorCode == 403) {
                    _tokenToastData.value = Unit
                    setRefreshToken()
                } else {
                    _errorToastData.value = Unit
                }
            }
            else -> {
                isAllSuccessful = false
            }
        }

        // 2. 프로필 이미지 업데이트
        if(modifyProfileImgData.value == true){
            myProfileData.value?.profileImg?.let {
                val imageResponse = usersRepository.setMyProfileImg(token, it)
                when (imageResponse) {
                    is ApiResponse.Success -> { /* nothing to do here */ }
                    is ApiResponse.Error -> {
                        isAllSuccessful = false
                        if (imageResponse.errorCode == 403) {
                            _tokenToastData.value = Unit
                            setRefreshToken()
                        } else {
                            _errorToastData.value = Unit
                        }
                    }
                }
            }

        }

        // 3. 최종 상태 결정
        _setProfileData.value = isAllSuccessful
    }



    fun setRefreshToken() = viewModelScope.launch {
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