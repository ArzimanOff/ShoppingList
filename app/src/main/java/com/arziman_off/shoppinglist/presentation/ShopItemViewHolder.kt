package com.arziman_off.shoppinglist.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arziman_off.shoppinglist.R

class ShopItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val tvName = itemView.findViewById<TextView>(R.id.tvName)
    val tvCount = itemView.findViewById<TextView>(R.id.tvCount)
}