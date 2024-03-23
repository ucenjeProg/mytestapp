package com.mycustomappapply.wotttoo.adapters

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.databinding.NotificationItemBinding
import com.mycustomappapply.wotttoo.models.Notification
import com.mycustomappapply.wotttoo.utils.toFormattedNumber

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    var onNotificationClickListener: ((String?) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: NotificationItemBinding = NotificationItemBinding.bind(itemView)

        init {
            binding.root.setOnClickListener {
                onNotificationClickListener?.let {
                    it(differ.currentList[adapterPosition].likedQuoteId)
                }
            }
        }

        fun bindData(notification: Notification) {
            binding.otherPeopleCount.text = "+${(notification.likeCount - 1).toFormattedNumber()}"
            val htmlText: Spanned = Html.fromHtml("${notification.likeCount} people liked the quote you added")
            binding.notificationText.text = htmlText
            if (notification.userPhoto != null && notification.userPhoto != "") {
                binding.notificationUserPhoto.load(notification.userPhoto)
            } else {
                binding.notificationUserPhoto.load(R.drawable.user)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NotificationAdapter.ViewHolder,
        position: Int
    ) {
        holder.bindData(differ.currentList[position])
    }

    private val diffCallback: DiffUtil.ItemCallback<Notification> = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Notification,
            newItem: Notification
        ): Boolean {
            return oldItem == newItem
        }

    }

    private val differ: AsyncListDiffer<Notification> = AsyncListDiffer(this, diffCallback)

    fun setData(
        newList: List<Notification>
    ) {
        differ.submitList(newList)
    }

    override fun getItemCount(): Int = differ.currentList.size
}