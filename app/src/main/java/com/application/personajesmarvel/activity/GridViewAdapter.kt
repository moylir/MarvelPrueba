package com.application.personajesmarvel.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.application.personajesmarvel.R
import com.application.personajesmarvel.global.Hero
import java.util.*

class GridViewAdapter(private val mContext: Context, list: ArrayList<Hero>) : BaseAdapter() {
    private val imageList = ArrayList<String>()
    private val idList = ArrayList<Int>()
    private val nameList = ArrayList<String>()
    private val options: DisplayImageOptions
    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val inflater = mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.row_gridview, null)
        view.tag = idList[position]
        val img_row = view.findViewById<ImageView>(R.id.img_row)
        val img_text = view.findViewById<TextView>(R.id.img_text)
        img_text.text = nameList[position]
        img_text.textSize = 20f
        img_text.setTextColor(Color.BLACK)
        img_text.textAlignment = View.TEXT_ALIGNMENT_CENTER
        val picturePath = imageList[position]
        ImageLoader.getInstance().displayImage(picturePath, img_row, options)
        return view
    }

    init {
        for (i in list.indices) {
            imageList.add(list[i].path)
            idList.add(list[i].id)
            nameList.add(list[i].name)
        }
        options = DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build()
    }
}