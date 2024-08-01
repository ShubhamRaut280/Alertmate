package com.shubham.emergencyapplication.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shubham.emergencyapplication.Models.User
import com.shubham.emergencyapplication.Repositories.UserRepository
import com.shubham.emergencyapplication.Callbacks.ResponseCallBack
import com.shubham.emergencyapplication.Repositories.UserRepository.getFamilyMembers
import kotlinx.coroutines.launch

class HomeFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val _data = MutableLiveData<List<User>>()

    val data: LiveData<List<User>> get() = _data


    init {
        fetchFamilyMembers()
    }

    private fun fetchFamilyMembers() {
        viewModelScope.launch {
            getFamilyMembers(getApplication(), object : ResponseCallBack<List<User>> {
                override fun onSuccess(response: List<User>?) {
                    if (!response.isNullOrEmpty()) {
                        _data.value = response!!
                    }else _data.value = mutableListOf<User>()
                }
                override fun onError(error: String?) {

                }

            })
        }
    }
}
