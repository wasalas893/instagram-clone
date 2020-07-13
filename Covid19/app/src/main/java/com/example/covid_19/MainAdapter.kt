package com.example.covid_19

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.video_row.view.*

class MainAdapter(val homeFeed:HomeFeed) :RecyclerView.Adapter<CustomViewHolder>(){
    val videoTitles= listOf("Count  25","Coount 50","Count 785")
    //numberOfItem

    override fun getItemCount(): Int {
        return  homeFeed.data.hospital_data.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater=LayoutInflater.from(parent?.context)
        val cellForRow=layoutInflater.inflate(R.layout.video_row,parent,false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {



        val video=homeFeed.data.hospital_data.get(position)



        holder?.view?.textView_video_title?.text=video.hospital.name

        holder?.view?.text_hospital?.text=video.treatment_total.toString()

        holder?.view?.textView6?.text=video.hospital_id.toString()

        holder?.view?.textView8?.text=video.cumulative_local.toString()

        holder?.view?.textView10?.text=video.cumulative_foreign.toString()

        holder?.view?.textView12?.text=video.cumulative_total.toString()




    }
}
class CustomViewHolder(val view:View):RecyclerView.ViewHolder(view){

}