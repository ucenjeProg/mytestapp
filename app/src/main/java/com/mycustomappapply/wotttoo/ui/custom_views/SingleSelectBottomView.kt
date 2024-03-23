package com.mycustomappapply.wotttoo.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.SingleSelectBottomViewBinding

class SingleSelectBottomView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 1
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var selectedState = Item.None

    enum class Item { Image, Text, Color, None }

    var onImageClick: () -> Unit = {}
    var onTextClick: () -> Unit = {}
    var onColorClick: () -> Unit = {}
    var onSameItemClick: () -> Unit = {}


    private val items: MutableList<TextView> by lazy {
        mutableListOf(
            binding.itemColor,
            binding.itemImage,
            binding.itemTextStyle
        )
    }

    private val binding: SingleSelectBottomViewBinding =
        com.mycustomappapply.wotttoo.databinding.SingleSelectBottomViewBinding.inflate(
            LayoutInflater.from(
                context
            ), this, true
        )

    init {
        initClickListeners()
    }

    fun reset() {
        val default: Int = ContextCompat.getColor(context, R.color.colorPrimaryText)
        selectedState = Item.None
        items.forEach {
            it.setTextColor(default)
            it.compoundDrawables[1]?.setTint(default)
        }
    }

    private fun sameItemClicked() {
        setState(Item.None)
        onSameItemClick()
    }

    private fun initClickListeners(): Unit = with(binding) {
        itemColor.setOnClickListener {
            if (selectedState == Item.Color) {
                sameItemClicked()
            } else {
                setState(Item.Color)
                onColorClick()
            }
        }
        itemImage.setOnClickListener {
            if (selectedState == Item.Image) {
                sameItemClicked()
            } else {
                setState(Item.Image)
                onImageClick()
            }

        }
        itemTextStyle.setOnClickListener {
            if (selectedState == Item.Text) {
                sameItemClicked()
            } else {
                setState(Item.Text)
                onTextClick()
            }
        }
    }

    fun setState(
        state: Item
    ) {
        selectedState = state
        setSelectedStyle()
    }

    private fun setSelectedStyle(): Unit? = with(binding) {
        val blue: Int = ContextCompat.getColor(context, R.color.blue)
        val default: Int = ContextCompat.getColor(context, R.color.colorPrimaryText)
        items.forEach {
            it.setTextColor(default)
            it.compoundDrawables[1]?.setTint(default)
        }
        val item: TextView? = when (selectedState) {
            Item.Text -> itemTextStyle
            Item.Image -> itemImage
            Item.Color -> itemColor
            Item.None -> null
        }
        item?.setTextColor(blue)
        item?.compoundDrawablesRelative?.run { this[1]?.setTint(blue) }
    }


}