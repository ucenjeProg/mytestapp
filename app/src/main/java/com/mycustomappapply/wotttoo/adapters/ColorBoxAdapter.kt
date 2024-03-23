package com.mycustomappapply.wotttoo.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.QuoteImageStyleItemBinding

class ColorBoxAdapter(
    val photoStyleColors: List<String>
) : RecyclerView.Adapter<ColorBoxAdapter.ViewHolder>() {

    var onColorBoxClickedListener: ((View, String) -> Unit)? = null

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding: QuoteImageStyleItemBinding = QuoteImageStyleItemBinding.bind(itemView)

        init {
            binding.colorBox.setOnClickListener { view: View ->
                onColorBoxClickedListener?.let { callback: (View, String) -> Unit ->
                    callback(view, photoStyleColors[adapterPosition])
                }
            }
        }

        fun bindData(
            color: String
        ) {
            binding.colorBox.background = ColorDrawable(Color.parseColor(color))
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorBoxAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.quote_image_style_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ColorBoxAdapter.ViewHolder,
        position: Int
    ) {
        holder.bindData(color = photoStyleColors[position])
    }

    override fun getItemCount(): Int = photoStyleColors.size

}
