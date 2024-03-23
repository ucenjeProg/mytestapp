package com.mycustomappapply.wotttoo.ui.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.LayoutOutlinedButtonBinding

class OutlinedButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attributeSet, defStyleAttr) {
    private var text: String = ""

    private var binding: LayoutOutlinedButtonBinding = LayoutOutlinedButtonBinding.bind(
        LayoutInflater.from(this.context)
            .inflate(R.layout.layout_outlined_button, this, true)
    )

    init {
        this.isClickable = true
        this.isFocusable = true
        val attributes: TypedArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.GoogleButton, 0, 0)
        text = attributes.getString(R.styleable.OutlinedButton_text) ?: ""
        initUi()
    }

    private fun initUi(): Unit = with(binding) {
        tvTitle.text = text
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.bg_outlined)
        isClickable = true
    }


}