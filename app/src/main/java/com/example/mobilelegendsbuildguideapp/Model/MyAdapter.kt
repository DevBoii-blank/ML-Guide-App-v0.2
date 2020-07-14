package com.example.mobilelegendsbuildguideapp.Model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilelegendsbuildguideapp.R

class MyAdapter(private val mycontext: FragmentActivity?, private val guide: ArrayList<Guide>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var HeroName: TextView = itemView.findViewById(R.id.Hero)
        var EarlyGuide: TextView = itemView.findViewById(R.id.tvEarlyGame)
        var MidGuide: TextView = itemView.findViewById(R.id.tvMidGame)
        var LateGuide: TextView = itemView.findViewById(R.id.tvLateGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mycontext).inflate(R.layout.layout_my_guide, parent,false))
    }

    override fun getItemCount(): Int {
        return guide.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.HeroName.text = guide[position].HeroName
        holder.EarlyGuide.text = guide[position].EarlyGame
        holder.MidGuide.text = guide[position].MidGame
        holder.LateGuide.text = guide[position].LateGame
    }
}