package com.example.shoutboxapp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EditMessageActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var date: TextView
    private lateinit var message: EditText
    private lateinit var deleteButton: ImageButton
    private lateinit var id: String
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_message)

        NetworkConnection(this).checkNetwork(this, this)

        scrollView = findViewById(R.id.scroll)
        scrollView.isVerticalScrollBarEnabled = false;

        username = findViewById(R.id.usernameEdit)
        date = findViewById(R.id.dateView)
        message = findViewById(R.id.messageEdit)


        val intentReceived = intent
        username.setText(intentReceived.getStringExtra("USERNAME"))
        date.text = intentReceived.getStringExtra("DATE")
        message.setText(intentReceived.getStringExtra("CONTENT"))

        id = intentReceived.getStringExtra("ID").toString()

        deleteButton = findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            deleteMessageOnServer()
            deleteButton.isClickable = false
            Toast.makeText(this, "Your message has been deleted", Toast.LENGTH_SHORT).show()
            finish()
        }


    }

    private fun editMessageOnServer() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://tgryl.pl/shoutbox/")
            .build()
            .create(MessageApi::class.java)


        retrofitBuilder.editMessage(id, message.text.toString(), username.text.toString()).enqueue(
            object : Callback<MessageModel> {
                override fun onResponse(
                    call: Call<MessageModel>,
                    response: Response<MessageModel>
                ) {
                    Log.d("onResponseEdit", "Edited")
                }

                override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                    Log.d("onFailureEdit", "Failed when editing" + t.message)
                }

            })
    }

    private fun deleteMessageOnServer() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://tgryl.pl/shoutbox/")
            .build()
            .create(MessageApi::class.java)


        retrofitBuilder.deleteMessage(id).enqueue(
            object : Callback<MessageModel> {
                override fun onResponse(
                    call: Call<MessageModel>,
                    response: Response<MessageModel>
                ) {
                    Log.d("onResponseDelete", "Deleted")
                }

                override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                    Log.d("onFailureDelete", "Failed when deleting " + t.message)
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        if (message.text.isNotEmpty() && deleteButton.isClickable) {
            editMessageOnServer()
        }
    }
}