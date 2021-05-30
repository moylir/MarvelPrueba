package com.application.personajesmarvel.global

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }

    fun showToast(activity: Context?, strMsg: String?) {
        val toast = Toast.makeText(activity, strMsg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun MD5String(md5: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(md5.toByteArray())
            val sb = StringBuffer()
            for (i in 0..array.size-1) {
                sb.append(Integer.toHexString(array[i].toInt() and 0xFF or 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {

        }



    return null
    }
}