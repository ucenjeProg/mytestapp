package com.mycustomappapply.wotttoo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.models.UserMessage


class MessageListAdapter(
    private val context: Context,
    private val messageList: List<UserMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.message.isNotEmpty()) VIEW_TYPE_MESSAGE_SENT else VIEW_TYPE_MESSAGE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> LayoutInflater.from(parent.context)
                .inflate(R.layout.firsrt_user, parent, false).let { SentMessageHolder(it) }
            VIEW_TYPE_MESSAGE_RECEIVED -> LayoutInflater.from(parent.context)
                .inflate(R.layout.secound_user, parent, false).let { ReceivedMessageHolder(it) }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
        return view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder.getItemViewType()) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    private class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_gchat_message_me)
        private val timeText: TextView = itemView.findViewById(R.id.text_gchat_date_me)

        fun bind(message: UserMessage) {
            messageText.text = message.message
            timeText.text = message.createdAt
        }
    }

    private class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_gchat_message_other)
        private val timeText: TextView = itemView.findViewById(R.id.text_gchat_date_other)
        private val nameText: TextView = itemView.findViewById(R.id.text_gchat_user_other)
       // private val profileImage: ImageView = itemView.findViewById(R.id.image_message_profile)

        fun bind(message: UserMessage) {
            messageText.text = message.nickname
            timeText.text = message.createdAt
            nameText.text = message.nickname
            // Utils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage)
        }
    }
}
