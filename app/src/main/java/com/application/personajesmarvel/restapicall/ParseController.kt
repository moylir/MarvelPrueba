package com.application.personajesmarvel.restapicall

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.application.personajesmarvel.global.Utils.isNetworkAvailable
import okhttp3.*
import java.io.File
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext

class ParseController(private val activity: Context?, private val httpMethod: HttpMethod,
                      private val map: MutableMap<String, String?>, isShowLoading: Boolean, loadingMsg: String,
                      listener: AsyncTaskCompleteListener) {
    private var listener: AsyncTaskCompleteListener? = null
    private var strURL: String? = null
    private val isShowLoading: Boolean
    private val loadingMsg: String
    private var statusCode = 0

    enum class HttpMethod {
        GET, POST, PUT, DELETE, FILEUPLOAD
    }

    // ParseController constructor,get all parameter via map,checking internet
    // connection
    fun process() {
        if (map.containsKey("url")) {
            strURL = map["url"]
            map.remove("url")
        }
        //Multipart body must have at least one part.
        if (map.size == 0) {
            map["device_type"] = "android"
        }
        //  getUnsafeOkHttpClient();
        AsyncHttpRequest().execute()
        Log.d("TYPE", httpMethod.toString())
    }

    // API call via async Task
    internal inner class AsyncHttpRequest : AsyncTask<Void?, Void?, String?>() {
        private val progressDialog: ProgressDialog? = ProgressDialog(activity)
        override fun onPreExecute() {
            super.onPreExecute()
            if (activity != null && activity is Activity
                    && !activity.isFinishing && isShowLoading) {
                progressDialog!!.setMessage(loadingMsg)
                progressDialog.setCancelable(false)
                progressDialog.show()
            }
        }



        override fun onPostExecute(result: String?) {
            if (progressDialog != null && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            checkResponse(result)
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: Void?): String? {
            return chooseWebService()
        }
    }

    fun chooseWebService(): String? {
        when (httpMethod) {
            HttpMethod.GET -> return callGETAPI()
            else -> {
            }
        }
        return null
    }

    fun callGETAPI(): String? {
        try {
            Log.d("STRURL", strURL!!)
            val httpBuider = HttpUrl.parse(strURL)!!.newBuilder()
            for (key in map.keys) {
                Log.e("Params", key + " = " + map[key])
                httpBuider.addQueryParameter(key, map[key])
            }
            val request = Request.Builder().url(httpBuider.build()).build()
            val response = configureHttpClient().newCall(request).execute()
            statusCode = response.code()
            return response.body()!!.string()
        } catch (ex: UnknownHostException) {
            ex.printStackTrace()
            return strInternet
        } catch (ex: SocketTimeoutException) {
            ex.printStackTrace()
            return strInternet
        } catch (ex: MalformedURLException) {
            ex.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
            return strInternet
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    // checking response is null or empty if not null call onTaskCompleted
    // method
    fun checkResponse(response: String?) {
        if (response == null || TextUtils.isEmpty(response.trim { it <= ' ' })
                || response.trim { it <= ' ' }.equals("null", ignoreCase = true)) {
            Log.e("Response in Controller", "Response is null")
            listener!!.onFailed(statusCode, "Response Is Empty Please Try Again Later")
        } else {
            Log.e("Response in Controller", response.trim { it <= ' ' })
            Log.e("=======", "=================================================================")
            listener!!.onSuccess(response)
        }
    }

    companion object {
        private const val strInternet = "No hay conectividad a internet"
        private val MEDIA_TYPE_PNG = MediaType.parse("image/jpg")
        private const val TIMEOUT = 15 * 60 * 1000
        fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder {
            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
                try {
                    val sc = SSLContext.getInstance("TLSv1.2")
                    sc.init(null, null, null)
                    client.sslSocketFactory(Tls12SocketFactory(sc.socketFactory))
                    val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build()
                    val specs: MutableList<ConnectionSpec> = ArrayList()
                    specs.add(cs)
                    specs.add(ConnectionSpec.COMPATIBLE_TLS)
                    specs.add(ConnectionSpec.CLEARTEXT)
                    client.connectionSpecs(specs)
                } catch (exc: Exception) {
                    Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
                }
            }
            return client
        }

        protected fun configureHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .cache(null)
                    .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true).connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
            return enableTls12OnPreLollipop(builder).build()
        }

        var t: Thread? = null
        fun fileVideoAndImageUploading(hashMap: MutableMap<String, String?>, k: Int): String? {
            try {
                val builder = Request.Builder()
                val multipartBody = MultipartBody.Builder()
                multipartBody.setType(MultipartBody.FORM)
                if (hashMap.containsKey("media")) {
                    val strFilePath = hashMap["media"]
                    hashMap.remove("media")
                    val file = File(strFilePath)
                    Log.d("file size", file.length().toString() + "")
                    if (file != null && file.exists()) {
                        Log.d("Params >> ", "file_data = $strFilePath")
                        multipartBody
                                .addFormDataPart("profile_photo", file.name,
                                        RequestBody.create(MEDIA_TYPE_PNG, file))
                    } else {
                        Log.d("file path error", "path not found: $strFilePath")
                    }
                }
                val strUrl = hashMap["url"]
                hashMap.remove("url")
                Log.d("STRURL >> ", strUrl!!)
                for (key in hashMap.keys) {
                    Log.d("Params", key + " = " + hashMap[key])
                    multipartBody.addFormDataPart(key, hashMap[key])
                }
                builder.url(strUrl)
                builder.post(multipartBody.build())
                val request = builder.build()
                val response = configureHttpClient().newCall(request).execute()
                return response.body()!!.string()
            } catch (ex: UnknownHostException) {
                ex.printStackTrace()
                return strInternet
            } catch (ex: SocketTimeoutException) {
                ex.printStackTrace()
                return strInternet
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
            } catch (e: SocketException) {
                e.printStackTrace()
                return strInternet
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun callPOSTAPI(map1: MutableMap<String, String?>): String? {
            try {
                val builder = Request.Builder()
                val multipartBody = MultipartBody.Builder()
                multipartBody.setType(MultipartBody.FORM)
                val strUrl = map1["url"]
                map1.remove("url")
                Log.e("STRURL >> ", strUrl!!)
                for (key in map1.keys) {
                    Log.e("Params", key + " = " + map1[key])
                    multipartBody.addFormDataPart(key, map1[key])
                }
                builder.url(strUrl)
                builder.post(multipartBody.build())
                val request = builder.build()
                val response = configureHttpClient().newCall(request).execute()
                return response.body()!!.string()
            } catch (ex: UnknownHostException) {
                ex.printStackTrace()
                return strInternet
            } catch (ex: SocketTimeoutException) {
                ex.printStackTrace()
                return strInternet
            } catch (ex: MalformedURLException) {
                ex.printStackTrace()
            } catch (e: SocketException) {
                e.printStackTrace()
                return strInternet
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    init {
        this.listener = listener
        this.isShowLoading = isShowLoading
        this.loadingMsg = loadingMsg
        statusCode = 0
        // is Internet Connection Available...
        if (isNetworkAvailable(activity)) {
            process()
        } else {
            listener.onFailed(100, strInternet)
        }
    }
}