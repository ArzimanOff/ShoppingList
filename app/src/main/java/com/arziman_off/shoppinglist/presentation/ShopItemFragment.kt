package com.arziman_off.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arziman_off.shoppinglist.R
import com.arziman_off.shoppinglist.domain.ShopItem
import com.arziman_off.shoppinglist.presentation.ShopItemActivity.Companion
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemFragment : Fragment() {

    private lateinit var viewModel: ShopItemViewModel


    private lateinit var btnSaveShopItem: MaterialButton
    private lateinit var btnDeleteItem: ImageButton
    private lateinit var tilItemName: TextInputLayout
    private lateinit var tilItemCount: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etCount: TextInputEditText

    private var screenMode: String = UNKNOWN_SCREEN_MODE
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    companion object {
        private const val LOG_TAG = "appNeedLogs"
        private const val MODE_NAME = "EXTRA_MODE_NAME"
        private const val SHOP_ITEM_ID = "EXTRA_SHOP_ITEM_ID"
        private const val MODE_ADD = "ADD"
        private const val MODE_EDIT = "EDIT"
        private const val UNKNOWN_SCREEN_MODE = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_NAME, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_NAME, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_shop_item,
            container,
            false
        )
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        setupViewModelObservers()
        launchMode()
    }

    private fun launchMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItemLD.observe(viewLifecycleOwner) {
            etName.setText(it.name.toString())
            etCount.setText(it.count.toString())
        }
        btnSaveShopItem.setOnClickListener {
            viewModel.editShopItem(
                etName.text?.toString(),
                etCount.text?.toString()
            )
        }
        btnDeleteItem.setOnClickListener {
            viewModel.shopItemLD.value?.let { item ->
                viewModel.deleteShopItem(
                    item
                )
            }
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

    private fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(MODE_NAME)){
            Log.d(LOG_TAG, "Мод не был передан на экран")
            throw RuntimeException("Мод не был передан на экран")
        }
        val mode = args.getString(MODE_NAME)
        if (mode != MODE_EDIT && mode != MODE_ADD){
            Log.d(LOG_TAG, "Передан неверный мод")
            throw RuntimeException("Передан неверный мод")
        }
        screenMode = mode
        Log.d(LOG_TAG, "Mode: $screenMode")
        if (screenMode == MODE_EDIT){
            if (!args.containsKey(SHOP_ITEM_ID)){
                Log.d(LOG_TAG, "Параметр id не был передан на экран редактирования")
                throw RuntimeException("Параметр id не был передан на экран редактирования")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }


        Log.d(LOG_TAG, "Mode: $screenMode")
        Log.d(LOG_TAG, "ShoItemId: $shopItemId")
    }


    private fun initViews(view: View) {
        btnSaveShopItem = view.findViewById(R.id.btnSaveShopItem)
        btnDeleteItem = view.findViewById(R.id.btnDeleteItem)

        tilItemName = view.findViewById(R.id.tilItemName)
        tilItemCount = view.findViewById(R.id.tilItemCount)
        etName = view.findViewById(R.id.etName)
        etCount = view.findViewById(R.id.etCount)

        btnDeleteItem.visibility = if (screenMode == MODE_ADD) {
            View.GONE
        } else {
            View.VISIBLE
        }

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        etCount.addTextChangedListener(object : TextWatcher {
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

        viewModel.errorInputName.observe(viewLifecycleOwner) {
            if (it) {
                tilItemName.error = "Ошибка ввода названия!"
            } else {
                tilItemName.error = null
            }
        }

        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            if (it) {
                tilItemCount.error = "Ошибка ввода количества!"
            } else {
                tilItemCount.error = null
            }
        }

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}