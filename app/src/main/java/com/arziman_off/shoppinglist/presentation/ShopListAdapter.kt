package com.arziman_off.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem

class ShopListAdapter: ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {
    companion object{
        const val ITEM_SHOP_ENABLED = 1
        const val ITEM_SHOP_DISABLED = 0

        const val MAX_POOL_SIZE = 15
    }

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null


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
        val shopItem = getItem(position)


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

    override fun getItemViewType(position: Int): Int {
        val shopItem = getItem(position)
        return if (shopItem.enabled) {
            ITEM_SHOP_ENABLED
        } else{
            ITEM_SHOP_DISABLED
        }
    }

}
