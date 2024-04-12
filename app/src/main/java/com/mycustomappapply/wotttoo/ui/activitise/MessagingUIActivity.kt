package com.mycustomappapply.wotttoo.ui.activitise

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycustomappapply.wotttoo.R
import com.mycustomappapply.wotttoo.adapters.MessageListAdapter
import com.mycustomappapply.wotttoo.models.UserMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MessagingUIActivity : AppCompatActivity() {
    lateinit var mMessageRecyclerView: RecyclerView
    lateinit var mMessageListAdapter: MessageListAdapter

    lateinit var btnSendMessage: Button
    lateinit var userMessage: UserMessage
    lateinit var editText: EditText
    val list: ArrayList<UserMessage> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging_uiactivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setData()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        editText=findViewById(R.id.edit_gchat_message)
        btnSendMessage=findViewById(R.id.button_gchat_send)
        btnSendMessage.setOnClickListener {
            val message=editText.text.toString()
            val currentDateTime = LocalDateTime.now()

            // Define the desired date time format
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Format the date time
            val createdAt = currentDateTime.format(formatter)
            val userMessage = UserMessage("", "", "", message, createdAt)
           // userMessage=UserMessage("","","",message,createdAt)
            list.add(userMessage)
            Log.d("TAG", "setData: "+list)
            mMessageRecyclerView = findViewById<View>(R.id.recycler_gchat) as RecyclerView
            mMessageListAdapter = MessageListAdapter(this, list)
            mMessageRecyclerView.layoutManager = LinearLayoutManager(this)
            mMessageRecyclerView.adapter = mMessageListAdapter

        }
    }
}