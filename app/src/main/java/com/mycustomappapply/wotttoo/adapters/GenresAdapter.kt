package com.mycustomappapply.wotttoo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.GenreItemLayoutBinding
import java.util.Locale

class GenresAdapter(
    val followingGenres: List<String>
) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    var onGenreClickListener: ((String) -> Unit)? = null

    private val diffItemCallback: DiffUtil.ItemCallback<String> = object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem

    }

    private val differ: AsyncListDiffer<String> = AsyncListDiffer(this, diffItemCallback)

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val binding: GenreItemLayoutBinding = GenreItemLayoutBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                onGenreClickListener?.invoke(differ.currentList[adapterPosition])
            }
        }

        fun bindData(
            genre: String
        ) {
            binding.genreName.text = "#$genre"
            binding.followingGenre.text = if (followingGenres.contains(genre.toLowerCase(Locale.ROOT))) "Following" else "Not Following"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.genre_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bindData(genre = differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setData(
        newList: List<String>
    ) {
        differ.submitList(newList)
    }
}
