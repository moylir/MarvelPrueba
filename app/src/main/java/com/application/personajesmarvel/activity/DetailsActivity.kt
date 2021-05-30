package com.application.personajesmarvel.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.application.personajesmarvel.R

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""
        initUI()
    }

    private fun initUI() {
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        tvTitle.text = intent.getStringExtra("nameCharacter")
        if (!intent.getStringExtra("description").equals( "")) {
            val descriptionHeader = findViewById<TextView>(R.id.descriptionheader)
            descriptionHeader.visibility = View.VISIBLE
            val description = findViewById<TextView>(R.id.description)
            description.visibility = View.VISIBLE
            description.text = intent.getStringExtra("description")
        }
        if (intent.getStringArrayListExtra("chapterList")!!.size > 0) {
            val aparicionesHeader = findViewById<TextView>(R.id.aparicionesheader)
            aparicionesHeader.visibility = View.VISIBLE
            val apariciones = findViewById<LinearLayout>(R.id.apariciones)
            apariciones.visibility = View.VISIBLE
            for (i in intent.getStringArrayListExtra("chapterList")!!.indices) {
                val tv = TextView(this)
                tv.text = """
                    ${intent.getStringArrayListExtra("chapterList")!![i]}
                    
                    """.trimIndent()
                apariciones.addView(tv)
            }
        }
    }
}