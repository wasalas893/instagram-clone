package com.example.covid_19

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*

import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recylerView_main.layoutManager=LinearLayoutManager(this)
       // recylerView_main.adapter=MainAdapter()

        fetchJson()


        }
    fun  fetchJson(){
        var url="https://www.hpb.health.gov.lk/api/get-current-statistical"

        val request=Request.Builder().url(url).build()

        val client = OkHttpClient().also {
            it.newCall(request).enqueue(object :Callback{
                override fun onResponse(call: Call, response: Response) {
                    val body= response.body?.string()


                    println(body)
                    val gson=GsonBuilder().create()
                    val homeFeed=gson.fromJson(body,HomeFeed::class.java)


                    runOnUiThread {
                        recylerView_main.adapter=MainAdapter(homeFeed)
                    }



                }

                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }




            })
        }


    }

        

}
class HomeFeed(val data:data)

class data(val hospital_data:List<hospital_data>,val local_new_cases:Int,val local_total_cases:Int,val local_deaths:Int,val local_recovered:Int)

class hospital_data(val treatment_total:Int,val cumulative_total:Int,val cumulative_local:Int,val cumulative_foreign:Int,val hospital:hospital,val hospital_id:Int)

class hospital(val id:Int,val name:String)
























