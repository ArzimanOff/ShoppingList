package com.arziman_off.shoppinglist.data

import com.arziman_off.shoppinglist.domain.ShopItem
import com.arziman_off.shoppinglist.domain.ShopListRepository

object ShopListRepositoryImpl: ShopListRepository {

    private val shopList = mutableListOf<ShopItem>()
    private var autoIncrementId = 0

    init {
        for (i in 0 until 10) {
            val item = ShopItem(
                "Name #$i",
                i,
                true
            )
            addShopItem(item)
        }
    }


    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID){
            shopItem.id = autoIncrementId++
        }
        shopList.add(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldItem: ShopItem = getShopItem(shopItem.id)
        deleteShopItem(oldItem)
        addShopItem(shopItem)
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        return shopList.find {
            it.id == shopItemId
        } ?: throw RuntimeException("Элемент с id $shopItemId не найден")
    }

    override fun getShopList(): List<ShopItem> {
        return shopList.toList()
    }
}