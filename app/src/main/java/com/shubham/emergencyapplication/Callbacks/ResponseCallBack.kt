package com.shubham.emergencyapplication.Callbacks

interface ResponseCallBack<T> {
    fun onSuccess(response: T?)
    fun onError(error: String?)
}
