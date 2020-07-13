package com.example.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private  const val BASE_URL="https://covidtracking.com/api/v1/"
private  const val TAG="MainActivity"
private const val  ALL_STATES="All Citys"
class MainActivity : AppCompatActivity() {


    private lateinit var adapter: CovidSparkAdapter
    private lateinit var perStateDailyData: Map<String, List<CovidData>>
    private lateinit var nationalDailyData: List<CovidData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson=GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit=Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val covidService=retrofit.create(CovidService::class.java)
        //Fetch the national data
        covidService.getNationalData().enqueue(object :Callback<List<CovidData>>{
            override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
               Log.e(TAG,"onFailure $t")
            }

            override fun onResponse(call: Call<List<CovidData>>, response: Response<List<CovidData>>) {
               Log.i(TAG,"onResponse $response")
                val nationalData=response.body()
                if(nationalData==null){
                    Log.w(TAG,"Did not receive a valid response body")
                    return
                }
                setupEventListeners()
                nationalDailyData=nationalData.reversed()
                Log.i(TAG,"update graph with national data $nationalDailyData")
                updateDisplayWithData(nationalDailyData)
            }

        })

        //Fetch the state data
        covidService.getStatesData().enqueue(object :Callback<List<CovidData>>{
            override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
                Log.e(TAG,"onFailure $t")
            }

            override fun onResponse(call: Call<List<CovidData>>, response: Response<List<CovidData>>) {
                Log.i(TAG,"onResponse $response")
                val statesData=response.body()
                if(statesData==null){
                    Log.w(TAG,"Did not receive a valid response body")
                    return
                }
                perStateDailyData=statesData.reversed().groupBy { it.state }
                Log.i(TAG,"update spinner with state names")

                //update state
                updateSpinnerWithStateData(perStateDailyData.keys)
            }

        })
    }

    private fun updateSpinnerWithStateData(stateNames: Set<String>) {
        val stateAbbreviationList=stateNames.toMutableList()
        stateAbbreviationList.sort()
        stateAbbreviationList.add(0,ALL_STATES)

        //add spinner
        spinnerSelect.attachDataSource(stateAbbreviationList)
        spinnerSelect.setOnSpinnerItemSelectedListener { parent, _, position, _ ->

            val selectedState=parent.getItemAtPosition(position) as String
            val selectData=perStateDailyData[selectedState]?:nationalDailyData
            updateDisplayWithData(selectData)
        }
    }



    private fun setupEventListeners() {
        //add a listener for the user scrubbing on the chart
        sparkView.isScrubEnabled=true
        sparkView.setScrubListener { itemData->
            if(itemData is CovidData){
                updateInfoForDate(itemData)
            }
        }
        //radio buttons
        radioGroupTimeSelection.setOnCheckedChangeListener { _, checkedId ->
            adapter.daysAgo=when(checkedId){
                R.id.radioButtonWeek->TimeScale.WEEK
                R.id.radioButtonMonth->TimeScale.MONTH
                else->TimeScale.MAX
            }
            adapter.notifyDataSetChanged()
        }
        radioGroupSelection.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.radioButtonNagive->updateDisplayMetric(Metric.NEGATIVE)
                R.id.radioButtonPositive->updateDisplayMetric(Metric.POSITIVE)
                R.id.radioButtonDeath->updateDisplayMetric(Metric.DEATH)
            }
        }
    }
    private  fun updateDisplayMetric(metric: Metric){
        adapter.metric=metric
        adapter.notifyDataSetChanged()
    }

    private fun updateDisplayWithData(dailyData: List<CovidData>) {
        //create an ew Sparkadapter wih the data
        adapter=CovidSparkAdapter(dailyData)
        sparkView.adapter=adapter
        //update radio button to select the positive cases and max time by default
        radioButtonPositive.isChecked=true
        radioButtonMax.isChecked=true
        //display metric for the  most recent date

       updateInfoForDate(dailyData.last())
    }

    private fun updateInfoForDate(covidData: CovidData) {
        val numCases=when(adapter.metric){
            Metric.NEGATIVE->covidData.negativeIncrease
            Metric.POSITIVE->covidData.positiveIncrease
            Metric.DEATH->covidData.deathIncrease
        }

          tvMetricLable.text=NumberFormat.getInstance().format(numCases)
          val outputDateFormat=SimpleDateFormat("MMM dd,yyyy",Locale.US)
        tvDateLable.text=outputDateFormat.format(covidData.dateChecked)
    }


}
