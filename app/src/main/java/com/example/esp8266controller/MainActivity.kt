package com.example.esp8266controller

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.esp8266controller.R.*
import com.example.esp8266controller.R.id.*
import com.example.esp8266controller.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var request: Request
    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pref = getSharedPreferences("MyPref", MODE_PRIVATE)
        onClickSaveIP()
        getIP()
        binding.apply {
            bLed1.setOnClickListener(onClickListener())
            bLed2.setOnClickListener(onClickListener())
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sync) post("temperature")
        return true
    }

    private fun onClickListener(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
                bLed1 -> {post("led1")}
                bLed2 -> {post("led2")}
            }
        }
    }

    private fun getIP() = with(binding){
        val ip = pref.getString("ip","")
        if(ip != null){
            if(ip.isNotEmpty())
                edip.setText(ip)
        }
    }

    private fun onClickSaveIP() = with(binding){
        bSaved.setOnClickListener {
            if (edip.text.isNotEmpty()) saveIp(edip.text.toString())
        }
    }


    private fun saveIp(ip: String){
        val editor = pref.edit()
        editor.putString("ip",ip)
        editor.apply()
    }

    // Func to send and receive data
    private fun post(post: String){
        Thread{
           request = Request.Builder().url("http://${binding.edip.text}/$post").build()
            try {
                var response = client.newCall(request).execute()
                if(response.isSuccessful){
                    val resultText = response.body()?.string()
                    runOnUiThread{
                        val temp = resultText + "26.94C°"
                        binding.tvTemp.text = temp
                    }
                }
            } catch (i: IOException){

            }



        }.start()

    }

}


