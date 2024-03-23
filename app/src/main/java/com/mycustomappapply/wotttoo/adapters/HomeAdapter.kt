package com.mycustomappapply.wotttoo.adapters

import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.HomeAdapterItemBinding
import com.mycustomappapply.wotttoo.models.Quote

class HomeAdapter() : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var currentUserId: String? = null

    var onUserClickListener: ((String) -> Unit)? = null
    var onQuoteOptionsClickListener: ((Quote) -> Unit)? = null
    var onLikeClickListener: ((Quote) -> Unit)? = null
    var onDownloadClickListener: ((Quote) -> Unit)? = null
    var onShareClickListener: ((Quote) -> Unit)? = null


    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var binding: HomeAdapterItemBinding = HomeAdapterItemBinding.bind(itemView)

        init {
            binding.userPhotoImageView.setOnClickListener(this)
            binding.postOptionsBtn.setOnClickListener(this)
            binding.likeBtn.setOnClickListener(this)
            binding.downloadButton.setOnClickListener(this)
            binding.shareBtn.setOnClickListener(this)
        }

        fun bindData(): Unit = with(binding) {
            val quote: Quote = differ.currentList[adapterPosition]

            // state 1 mean publish
            if (quote.attributes.state == 1) {
                quoteTextView.text = Html.fromHtml(quote.attributes.text) as? SpannableStringBuilder
            }


        }

        override fun onClick(
            v: View?
        ) {
            when (v?.id) {
                R.id.likeBtn -> {
                    val quote: Quote = differ.currentList[adapterPosition]
                    val likes: MutableList<String> = quote.attributes.usersThatLikeThisPost.toMutableList()
                    if (quote.attributes.liked.isNotBlank()) {
                        binding.likeBtn.setImageResource(R.drawable.ic_like_blue)
                        currentUserId?.let { likes.add(it) }
                    } else {
                        binding.likeBtn.setImageResource(R.drawable.ic_like)
                        likes.remove(currentUserId)
                    }
                    quote.attributes.liked = if (quote.attributes.liked == "true") "false" else "true"
                    quote.attributes.usersThatLikeThisPost = likes.toList()
                    binding.likeCountTextView.text = quote.attributes.likes_count
                    onLikeClickListener?.let { name: (Quote) -> Unit ->
                        name(quote)
                    }
                    if (quote.attributes.liked.isNotBlank()) {
                        binding.likeBtn.startAnimation(
                            AnimationUtils.loadAnimation(
                                binding.likeBtn.context,
                                R.anim.bouncing_anim
                            )
                        )
                    }
                }

                R.id.username_textView, R.id.userPhotoImageView -> onUserClickListener?.invoke(differ.currentList[adapterPosition].id)

                R.id.postOptionsBtn -> onQuoteOptionsClickListener?.invoke(differ.currentList[adapterPosition])

                R.id.downloadButton -> onDownloadClickListener?.invoke(differ.currentList[adapterPosition])

                R.id.shareBtn -> onShareClickListener?.invoke(differ.currentList[adapterPosition])

            }
        }
    }

    private val diffCallback: DiffUtil.ItemCallback<Quote> = object : DiffUtil.ItemCallback<Quote>() {

        override fun areItemsTheSame(
            oldItem: Quote,
            newItem: Quote
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Quote,
            newItem: Quote
        ): Boolean = oldItem == newItem

    }

    private val differ: AsyncListDiffer<Quote> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeAdapter.ViewHolder {
        val view: View =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.home_adapter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HomeAdapter.ViewHolder,
        position: Int
    ) {
        holder.bindData()
    }

    override fun getItemViewType(
        position: Int
    ): Int = 0 //if (differ.currentList[position].backgroundUrl != null) BG_IMAGE else BG_COLOR

    override fun getItemCount(): Int = differ.currentList.size

    fun setData(
        newQuoteList: List<Quote>?
    ) {
        if (newQuoteList == null) return
        differ.submitList(newQuoteList)
    }

}
