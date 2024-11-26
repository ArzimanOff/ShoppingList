package com.arziman_off.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arziman_off.shoppinglist.data.ShopListRepositoryImpl
import com.arziman_off.shoppinglist.domain.AddShopItemUseCase
import com.arziman_off.shoppinglist.domain.DeleteShopItemUseCase
import com.arziman_off.shoppinglist.domain.EditShopItemUseCase
import com.arziman_off.shoppinglist.domain.GetShopItemUseCase
import com.arziman_off.shoppinglist.domain.ShopItem

class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ShopListRepositoryImpl(application)

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)

    private val _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _shopItemLD = MutableLiveData<ShopItem>()
    val shopItemLD: LiveData<ShopItem>
        get() = _shopItemLD

    private val _closeScreen = MutableLiveData<Unit>()
    val closeScreen: LiveData<Unit>
        get() = _closeScreen


    fun getShopItem(shopItemId: Int) {
        _shopItemLD.value = getShopItemUseCase.getShopItem(shopItemId)
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }


    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItemLD.value?.let {
                val shopItem = it.copy(name = name, count = count)
                editShopItemUseCase.editShopItem(shopItem)
                finishWork()
            }
        }
    }

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
        finishWork()
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var res = true
        if (name.isBlank()) {
            _errorInputName.value = true
            res = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            res = false
        }
        return res
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _closeScreen.value = Unit
    }


}