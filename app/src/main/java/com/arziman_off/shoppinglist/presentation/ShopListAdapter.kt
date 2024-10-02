package com.arziman_off.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem

class ShopListAdapter: RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {
    companion object{
        const val ITEM_SHOP_ENABLED = 1
        const val ITEM_SHOP_DISABLED = 0

        const val MAX_POOL_SIZE = 15
    }

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    var shopList = listOf<ShopItem>()
        set(value) {
            val callback = ShopListDiffCallback(shopList, value)
            val result = DiffUtil.calculateDiff(callback)
            result.dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val res = when (viewType){
            ITEM_SHOP_ENABLED -> R.layout.item_shop_enabled
            ITEM_SHOP_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Незнакомый тип данных $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(
            res,
            parent,
            false
        )
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = shopList[position]


        holder.tvName.text = shopItem.name
        holder.tvCount.text = shopItem.count.toString()

        holder.itemView.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
        holder.itemView.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    override fun getItemViewType(position: Int): Int {
        val shopItem = shopList[position]
        return if (shopItem.enabled) {
            ITEM_SHOP_ENABLED
        } else{
            ITEM_SHOP_DISABLED
        }
    }

    class ShopItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvCount = itemView.findViewById<TextView>(R.id.tvCount)
    }

    interface OnShopItemLongClickListener{
        fun onShopItemLongClick(shopItem: ShopItem)
    }

    interface OnShopItemClickListener{
        fun onShopItemClick(shopItem: ShopItem)
    }
}
