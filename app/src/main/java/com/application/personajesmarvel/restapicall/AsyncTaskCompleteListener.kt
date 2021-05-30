package com.application.personajesmarvel.restapicall

interface AsyncTaskCompleteListener {
    fun onSuccess(response: String?)
    fun onFailed(statusCode: Int, msg: String?)
}