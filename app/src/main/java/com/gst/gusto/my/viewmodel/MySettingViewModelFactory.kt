package com.gst.gusto.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.api.service.AuthApi
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.datasource.AuthDataSource
import com.gst.gusto.datasource.UsersDataSource
import com.gst.gusto.repository.AuthRepository
import com.gst.gusto.repository.AuthRepositoryImpl
import com.gst.gusto.repository.UsersRepository
import com.gst.gusto.repository.UsersRepositoryImpl
import java.lang.IllegalArgumentException

class MySettingViewModelFactory(): ViewModelProvider.Factory {
    // private val usersRepository = UsersRepository.create()

    // private val authRepository = AuthRepository.create()

    private val usersRepository = UsersRepositoryImpl(
        UsersDataSource(RetrofitInstance.createService(UsersApi::class.java))
    )

    private val authRepository = AuthRepositoryImpl(
        AuthDataSource(RetrofitInstance.createService(AuthApi::class.java))
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySettingViewModel::class.java)){
            return MySettingViewModel(usersRepository, authRepository) as T
        }
        throw IllegalArgumentException("unknown ViewModel class")
    }
}