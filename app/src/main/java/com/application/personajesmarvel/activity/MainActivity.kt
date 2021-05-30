package com.application.personajesmarvel.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import com.application.personajesmarvel.R
import com.application.personajesmarvel.global.GlobalConstant
import com.application.personajesmarvel.global.Hero
import com.application.personajesmarvel.global.Utils
import com.application.personajesmarvel.restapicall.AsyncTaskCompleteListener
import com.application.personajesmarvel.restapicall.ParseController
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private var grid_view: GridView? = null
    private val comicList = ArrayList<Hero>()
    var spinnerposition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""
        initUI()
    }

    private fun initUI() {
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        tvTitle.text = "Personajes Marvel"
        val spinner = findViewById<Spinner>(R.id.listView)
        setListeners(spinner)
        grid_view = findViewById(R.id.grid_view)
        grid_view!!.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l -> getCharacterOrCharacters(spinnerposition, view.tag.toString(),null) })


        getPagesAndCharacters(spinner,null)
    }

    fun setListeners(spinner: Spinner) {

        val input = findViewById<EditText>(R.id.edit)
        val buttonSearch = findViewById<Button>(R.id.buttonBuscar)

        buttonSearch.setOnClickListener{
            if (input.text.toString().length>0) {
                getPagesAndCharacters(spinner,input.text.toString())
            }
            else
            {
                Utils.showToast(this,resources.getString(R.string.noempty))
            }
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            input.setText("")
            getPagesAndCharacters(spinner,null) }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (Utils.isNetworkAvailable(baseContext)) {
                    if (!dontfire) {
                        getCharacterOrCharacters(i, null,null)
                        spinnerposition = i
                    } else {
                        dontfire = false
                    }
                } else {
                    spinner.setSelection(spinnerposition, false)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    private fun setSpinner(total: Int, spinner: Spinner)
    {
        val array = ArrayList<Int>()
        for (j in 0 until (total / 100)+1) {
            array.add(j + 1)
        }
        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        dontfire = true
        spinner.setSelection(spinnerposition, false)
    }

    var dontfire = false
    private fun getPagesAndCharacters(spinner: Spinner, filter: String?) {

        if (filter==null) {
            val map2: MutableMap<String, String?> = HashMap()
            map2["url"] = GlobalConstant.URL + GlobalConstant.COMICS
            map2["ts"] = "1"
            map2["apikey"] = GlobalConstant.PUBLIC_KEY
            map2["hash"] = Utils.MD5String("1" + GlobalConstant.PRIVATE_KEY + GlobalConstant.PUBLIC_KEY)
            Utils.hideKeyboard(this)
            ParseController(this, ParseController.HttpMethod.GET, map2,
                    true, resources.getString(R.string.loadingpages),
                    object : AsyncTaskCompleteListener {


                        override fun onSuccess(response: String?) {
                            try {
                                val objMain = JSONObject(response)
                                val code = objMain.getString("code")
                                if (code == "200") {
                                    val objData = objMain.getJSONObject("data")
                                    val total = objData.getInt("total")
                                   setSpinner(total,spinner)
                                    getCharacterOrCharacters(spinnerposition, null,null)
                                }
                            } catch (e: Exception) {
                            }
                        }

                        override fun onFailed(statusCode: Int, msg: String?) {
                            Utils.showToast(this@MainActivity, msg)
                        }
                    })
        }
        else
        {
            getCharacterOrCharacters(spinnerposition, null,filter)
        }
    }

    private fun getCharacterOrCharacters(i: Int, idCharacter: String?, filter: String?) {
        val map: MutableMap<String, String?> = HashMap()
        if (idCharacter == null) {
            map["url"] = GlobalConstant.URL + GlobalConstant.COMICS
            map["offset"] = (i * 100).toString()
            map["limit"] = "100"
        } else {
            map["url"] = GlobalConstant.URL + GlobalConstant.COMICS + "/" + idCharacter
        }
        map["ts"] = "1"
        map["apikey"] = GlobalConstant.PUBLIC_KEY
        map["hash"] = Utils.MD5String("1" + GlobalConstant.PRIVATE_KEY + GlobalConstant.PUBLIC_KEY)
        Utils.hideKeyboard(this)
        var elementloaded: String? = null
        if (idCharacter == null) {
            elementloaded = resources.getString(R.string.loadingcharacters)
        } else {
            elementloaded = resources.getString(R.string.loadingcharacter)

        }
        ParseController(this, ParseController.HttpMethod.GET, map,
                true,elementloaded,
                object : AsyncTaskCompleteListener {



                    override fun onSuccess(response: String?) {
                        try {
                            val objMain = JSONObject(response)
                            val code = objMain.getString("code")
                            if (code == "200") {
                                val objData = objMain.getJSONObject("data")
                                val jsonArray = objData.getJSONArray("results")
                                if (jsonArray.length() > 0) {
                                    comicList.clear()
                                    for (i in 0 until jsonArray.length()) {
                                        val `object` = jsonArray.getJSONObject(i)
                                        if (idCharacter == null) {
                                            val objectThumbnail = `object`.getJSONObject("thumbnail")
                                            val id = `object`.getInt("id")
                                            val name = `object`.getString("name")
                                            if ((filter==null) || ((filter!=null) && (name.toLowerCase().contains(filter.toLowerCase())))) {
                                                val path = objectThumbnail.getString("path")
                                                val extension = objectThumbnail.getString("extension")
                                                val hero = Hero("$path.$extension", id, name)
                                                comicList.add(hero)
                                            }

                                        } else {
                                            val chaptersList = ArrayList<String>()
                                            val nameCharacter = `object`.getString("name")
                                            val description = `object`.getString("description")
                                            val objectComics = `object`.getJSONObject("comics")
                                            val objectItems = objectComics.getJSONArray("items")
                                            for (j in 0 until objectItems.length()) {
                                                val elem = objectItems.getJSONObject(j)
                                                val name = elem.getString("name")
                                                chaptersList.add(name)
                                            }
                                            callActivity(nameCharacter, description, chaptersList)
                                        }
                                    }
                                    if (idCharacter == null) {
                                        setData()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailed(statusCode: Int, msg: String?) {
                        Utils.showToast(this@MainActivity, msg)
                    }
                })
    }

    private fun callActivity(nameCharacter: String, description: String, chapterList: ArrayList<String>) {
        val i = Intent(this, DetailsActivity::class.java)
        if (!description.equals("") || chapterList.size > 0) {
            i.putExtra("nameCharacter", nameCharacter)
            i.putExtra("description", description)
            i.putStringArrayListExtra("chapterList", chapterList)
            startActivity(i)
        } else {
            Utils.showToast(this, resources.getString(R.string.nodetail))
        }
    }

    private fun setData() {
        grid_view!!.adapter = GridViewAdapter(this, comicList)
    }
}