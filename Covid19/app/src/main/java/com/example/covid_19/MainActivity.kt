package com.example.covid_19

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apifuntion()
    }

    private fun apifuntion() {
        var url="https://covid19.mathdro.id/api"
        
        var request=Request.Builder().url(url).build()

        var client=OkHttpClient()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to excelute request")
            }

            override fun onResponse(call: Call, response: Response) {
               var body=response?.body()?.string()
                println(body)
                val gson=GsonBuilder().create()
                val homefeed=gson.fromJson(body,HomeFeed::class.java)
            }

        })
    }
}
class HomeFeed(val countries:List<countries>)

class countries(val name:String,val iso2:String,val iso3:String)






