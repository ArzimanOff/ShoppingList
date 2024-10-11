package com.arziman_off.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.arziman_off.shoppinglist.R
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {
    companion object {
        const val LOG_TAG = "appNeedLogs"
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var btnNewShopItem: MaterialButton
    private var shopItemContainer: FragmentContainerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shopItemContainer = findViewById(R.id.shopItemContainer)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initViews()
        setupRecyclerView()
        setupViewModelObservers()
    }

    private fun initViews(){
        btnNewShopItem = findViewById(R.id.btnNewShopItem)
        btnNewShopItem.setOnClickListener {
            if (!isOnePaneMode()){
                val fragment = ShopItemFragment.newInstanceAddItem()
                launchFragment(fragment)
            } else {
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            }
        }
    }

    private fun isOnePaneMode(): Boolean{
        return shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment){
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shopItemContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupViewModelObservers() {
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
        viewModel.deletedShopItemId.observe(this){
            if (it == viewModel.currentEditingShopItemId.value){
                supportFragmentManager.popBackStack()
            }
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
            if (!isOnePaneMode()){
                viewModel.startEditingShopItem(it)
                val fragment = ShopItemFragment.newInstanceEditItem(it.id)
                launchFragment(fragment)
            } else {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnableState(it)
        }
    }

    override fun onEditingFinished() {
        Toast.makeText(
            this,
            "Сохранено!",
            Toast.LENGTH_SHORT
        ).show()
        supportFragmentManager.popBackStack()
    }
}