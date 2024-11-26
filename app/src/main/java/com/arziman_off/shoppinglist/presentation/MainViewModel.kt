package com.arziman_off.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arziman_off.shoppinglist.data.ShopListRepositoryImpl
import com.arziman_off.shoppinglist.domain.DeleteShopItemUseCase
import com.arziman_off.shoppinglist.domain.EditShopItemUseCase
import com.arziman_off.shoppinglist.domain.GetShopListUseCase
import com.arziman_off.shoppinglist.domain.ShopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ShopListRepositoryImpl(application)

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    private val _deletedShopItemId = MutableLiveData<Int>()
    val deletedShopItemId: LiveData<Int>
        get() = _deletedShopItemId

    private val _currentEditingShopItemId = MutableLiveData<Int>()
    val currentEditingShopItemId: LiveData<Int>
        get() = _currentEditingShopItemId

    val shopList = getShopListUseCase.getShopList()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun deleteShopItem(shopItem: ShopItem){
        scope.launch {
            _deletedShopItemId.postValue(shopItem.id)
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }
    }

    fun startEditingShopItem(shopItem: ShopItem){
        _currentEditingShopItemId.postValue(shopItem.id)
    }

    fun changeEnableState(shopItem: ShopItem){
        scope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase.editShopItem(newItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }

}