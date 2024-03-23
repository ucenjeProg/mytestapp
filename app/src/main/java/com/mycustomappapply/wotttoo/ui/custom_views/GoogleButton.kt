package com.mycustomappapply.wotttoo.ui.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.LayoutGoogleButtonBinding
import com.mycustomappapply.wotttoo.utils.gone
import com.mycustomappapply.wotttoo.utils.visible

class GoogleButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attributeSet, defStyleAttr) {

    private var text: String = ""

    private var binding: LayoutGoogleButtonBinding = LayoutGoogleButtonBinding.bind(
        LayoutInflater.from(this.context)
            .inflate(R.layout.layout_google_button, this, true)
    )

    init {
        this.isClickable = true
        this.isFocusable = true
        val attributes: TypedArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.GoogleButton, 0, 0)
        text = attributes.getString(R.styleable.GoogleButton_text) ?: ""
        initUi()
    }

    private fun initUi(): Unit = with(binding) {
        tvText.text = text
    }

    fun showLoading(): Unit = with(binding) {
        root.isEnabled = false
        progressBar.visible()
        tvText.gone()
    }

    fun hideLoading(): Unit = with(binding) {
        progressBar.gone()
        tvText.visible()
        root.isEnabled = true
    }


}