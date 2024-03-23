package com.mycustomappapply.wotttoo.ui.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.LayoutProgressBarButtonBinding
import com.mycustomappapply.wotttoo.utils.gone
import com.mycustomappapply.wotttoo.utils.visible

class ProgressBarButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    var text: String = ""
        set(value: String) {
            field = value
            binding.tvNext.text = value
        }

    private var binding: LayoutProgressBarButtonBinding = LayoutProgressBarButtonBinding.bind(
        LayoutInflater.from(this.context)
            .inflate(R.layout.layout_progress_bar_button, this, true)
    )

    init {
        this.isClickable = true
        this.isFocusable = true
        val attributes: TypedArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.ProgressBarButton, 0, 0)
        text = attributes.getString(R.styleable.ProgressBarButton_text) ?: ""
    }


    fun showLoading(): Unit = with(binding) {
        root.isEnabled = false
        progressBar.visible()
        tvNext.gone()
    }

    fun hideLoading(): Unit = with(binding) {
        progressBar.gone()
        tvNext.visible()
        root.isEnabled = true
    }


}