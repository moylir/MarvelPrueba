package com.application.personajesmarvel.global

import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

class GlobalApp : MultiDexApplication() {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initImageLoader(this)
    }

    companion object {
        fun getPref(context: Context): SharedPreferences {
            return context.getSharedPreferences(GlobalConstant.PREFERENCE, Context.MODE_PRIVATE)
        }

        fun initImageLoader(context: Context?) {
            val config = ImageLoaderConfiguration.Builder(
                    context).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .build()
            ImageLoader.getInstance().init(config)
        }
    }
}