package com.shubham.emergencyapplication.Callbacks

interface ResponseCallBack {
    fun onSuccess(response: Any)
    fun onError(error: String?)
}