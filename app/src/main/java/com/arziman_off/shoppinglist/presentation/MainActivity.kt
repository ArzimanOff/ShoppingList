package com.arziman_off.shoppinglist.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOG_TAG = "appNeedLogs"
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var btnNewShopItem: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupRecyclerView()
        setupViewModelObservers()
        btnNewShopItem = findViewById(R.id.btnNewShopItem)
        btnNewShopItem.setOnClickListener {
            val intent = ShopItemActivity.newIntentAddItem(this)
            startActivity(intent)
        }
    }

    private fun setupViewModelObservers() {
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
    }



    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rvShopList)
        with(rvShopList) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.ITEM_SHOP_ENABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.ITEM_SHOP_DISABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeToDeleteListener(rvShopList)
    }

    private fun setupSwipeToDeleteListener(rvShopList: RecyclerView) {
        val itemSwapHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemSwapHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            Log.d(LOG_TAG, it.toString())
            val intent = ShopItemActivity.newIntentEditItem(this, it.id)
            startActivity(intent)
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }
}