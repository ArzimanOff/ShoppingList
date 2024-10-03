package com.arziman_off.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ShopItemActivity : AppCompatActivity() {
    companion object {
        private const val LOG_TAG = "appNeedLogs"
        private const val EXTRA_MODE_NAME = "EXTRA_MODE_NAME"
        private const val EXTRA_SHOP_ITEM_ID = "EXTRA_SHOP_ITEM_ID"
        private const val MODE_ADD = "ADD"
        private const val MODE_EDIT = "EDIT"

        fun newIntentAddItem(context: Context): Intent{
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE_NAME, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent{
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_MODE_NAME, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }

    }

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var mode: String

    private lateinit var btnSaveShopItem: MaterialButton
    private lateinit var btnDeleteItem: ImageButton
    private lateinit var etName: TextInputEditText
    private lateinit var etCount: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        mode = intent.getStringExtra(EXTRA_MODE_NAME).toString()
        Log.d(LOG_TAG, mode)
        initViews()
        setupEventListeners()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        setupViewModelObservers()
    }


    private fun initViews() {
        btnSaveShopItem = findViewById(R.id.btnSaveShopItem)
        btnDeleteItem = findViewById(R.id.btnDeleteItem)

        btnDeleteItem.visibility = if (mode == MODE_ADD) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setupEventListeners() {
        btnSaveShopItem.setOnClickListener {
            viewModel.addShopItem(
                etName.text.toString(), etCount.text.toString()
            )
        }
        btnDeleteItem.setOnClickListener {

        }
    }

    private fun setupViewModelObservers() {

        viewModel.errorInputName.observe(this) {

        }

        viewModel.errorInputCount.observe(this) {

        }

        viewModel.closeScreen.observe(this) {
            finish()
        }
    }


}