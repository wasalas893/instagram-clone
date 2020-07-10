package com.example.tracker

import android.graphics.RectF
import com.robinhood.spark.SparkAdapter


     class CovidSparkAdapter(private  val dailyData: List<CovidData>):SparkAdapter() {

         var metric=Metric.POSITIVE
         var daysAgo=TimeScale.MAX


         override fun getY(index: Int): Float {
            val chosenDayDate=dailyData[index]
             return when(metric){
                 Metric.NEGATIVE->chosenDayDate.negativeIncrease.toFloat()
                 Metric.POSITIVE->chosenDayDate.positiveIncrease.toFloat()
                 Metric.DEATH->chosenDayDate.deathIncrease.toFloat()
             }
             return chosenDayDate.positiveIncrease.toFloat()
         }

         override fun getItem(index: Int)=dailyData[index]

         override fun getCount()=dailyData.size

         override fun getDataBounds(): RectF {
             val bounds=super.getDataBounds()
               if(daysAgo!=TimeScale.MAX){
                   bounds.left=count -daysAgo.numDays.toFloat()

               }
                return  bounds
         }


     }
