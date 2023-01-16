package com.example.shoutboxapp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer

class ViewMessageActivity : AppCompatActivity() {
    private lateinit var username: TextView
    private lateinit var date: TextView
    private lateinit var message: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_message)

        NetworkConnection(this).checkNetwork(this, this)

        scrollView = findViewById(R.id.scroll)

        scrollView.isVerticalScrollBarEnabled = false

        username = findViewById(R.id.usernameView)
        date = findViewById(R.id.dateView)
        message = findViewById(R.id.messageView)

        val intentReceived = intent
        username.text = intentReceived.getStringExtra("USERNAME")
        date.text = intentReceived.getStringExtra("DATE")
        message.text = intentReceived.getStringExtra("CONTENT")


    }
}