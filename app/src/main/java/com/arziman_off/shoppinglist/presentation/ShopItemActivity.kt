package com.arziman_off.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.w3c.dom.Text

class ShopItemActivity : AppCompatActivity() {

    private lateinit var viewModel: ShopItemViewModel
    private var screenMode: String = UNKNOWN_SCREEN_MODE
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    private lateinit var btnSaveShopItem: MaterialButton
    private lateinit var btnDeleteItem: ImageButton
    private lateinit var tilItemName: TextInputLayout
    private lateinit var tilItemCount: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etCount: TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews()
        setupViewModelObservers()
        launchMode()
    }

    private fun launchMode() {
        when (screenMode){
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItemLD.observe(this){
            etName.setText(it.name.toString())
            etCount.setText(it.count.toString())
        }
        btnSaveShopItem.setOnClickListener {
            viewModel.editShopItem(
                etName.text?.toString(),
                etCount.text?.toString()
            )
        }
    }

    private fun launchAddMode() {
        btnSaveShopItem.setOnClickListener {
            viewModel.addShopItem(
                etName.text?.toString(),
                etCount.text?.toString()
            )
        }
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_MODE_NAME)) {
            Log.d(LOG_TAG, "Мод не был передан на экран")
            throw RuntimeException("Мод не был передан на экран")
        }
        val mode = intent.getStringExtra(EXTRA_MODE_NAME).toString()
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            Log.d(LOG_TAG, "Передан неверный мод")
            throw RuntimeException("Передан неверный мод")
        }
        screenMode = mode
        Log.d(LOG_TAG, "Mode: $screenMode")
        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                Log.d(LOG_TAG, "Параметр id не был передан на экран редактирования")
                throw RuntimeException("Параметр id не был передан на экран редактирования")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            Log.d(LOG_TAG, "Item id: $shopItemId")
        }
    }


    private fun initViews() {
        btnSaveShopItem = findViewById(R.id.btnSaveShopItem)
        btnDeleteItem = findViewById(R.id.btnDeleteItem)

        tilItemName = findViewById(R.id.tilItemName)
        tilItemCount = findViewById(R.id.tilItemCount)
        etName = findViewById(R.id.etName)
        etCount = findViewById(R.id.etCount)

        btnDeleteItem.visibility = if (screenMode == MODE_ADD) {
            View.GONE
        } else {
            View.VISIBLE
        }

        etName.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        etCount.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }


    private fun setupViewModelObservers() {

        viewModel.errorInputName.observe(this) {
            if (it){
                tilItemName.error = "Ошибка ввода названия!"
            } else {
                tilItemName.error = null
            }
        }

        viewModel.errorInputCount.observe(this) {
            if (it){
                tilItemCount.error = "Ошибка ввода количества!"
            } else {
                tilItemCount.error = null
            }
        }

        viewModel.closeScreen.observe(this) {
            finish()
        }
    }

    companion object {
        private const val LOG_TAG = "appNeedLogs"
        private const val EXTRA_MODE_NAME = "EXTRA_MODE_NAME"
        private const val EXTRA_SHOP_ITEM_ID = "EXTRA_SHOP_ITEM_ID"
        private const val MODE_ADD = "ADD"
        private const val MODE_EDIT = "EDIT"
        private const val UNKNOWN_SCREEN_MODE = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE_NAME, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE_NAME, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }

    }


}