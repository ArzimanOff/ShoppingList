package com.arziman_off.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem

class ShopListAdapter: RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    var shopList = listOf<ShopItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_shop_enabled,
            parent,
            false
        )
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = shopList[position]

        val status = if (shopItem.enabled){
            "Active"
        } else {
            "Not Active"
        }

        holder.tvName.text = "${shopItem.name} ${status}"
        holder.tvCount.text = shopItem.count.toString()

        holder.itemView.setOnLongClickListener {
            true
        }

    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class ShopItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvCount = itemView.findViewById<TextView>(R.id.tvCount)
    }
}
