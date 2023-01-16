package com.example.shoutboxapp3

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageAdapter(private var messages: MutableList<MessageModel>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

     inner class MessageViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {


        init {
            val shared = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
            val user = shared.getString("USERNAME", "")
            itemView.setOnClickListener {
                if(user.toString() == username.text.toString())  {
                    val intent = Intent(itemView.context, EditMessageActivity::class.java)
                    intent.putExtra("USERNAME", username.text.toString())
                    intent.putExtra("DATE", date.text.toString())
                    intent.putExtra("CONTENT", message.text.toString())
                    intent.putExtra("ID", id)

                    itemView.context.startActivity(intent)

                } else {
                    val intent = Intent(itemView.context, ViewMessageActivity::class.java)
                    intent.putExtra("USERNAME", username.text.toString())
                    intent.putExtra("DATE", date.text.toString())
                    intent.putExtra("CONTENT", message.text.toString())

                    itemView.context.startActivity(intent)
                }

            }
        }

        val username: TextView = itemView.findViewById(R.id.usernameEdit)
        val date: TextView = itemView.findViewById(R.id.dateView)
        val message: TextView = itemView.findViewById(R.id.messageEdit)
        lateinit var id: String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(itemView, itemView.context)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, pos: Int) {
        val mess = messages[pos]
        holder.username.text = mess.login
        val split = mess.date.split("T")
        val split1 = split[1].split(".")
        holder.date.text = split[0] + "  " + split1[0]
        holder.message.text = mess.content
        holder.id = mess.id
    }

    fun removeAt(position: Int, context: Context) {
        val shared = context.getSharedPreferences("shared preferences", AppCompatActivity.MODE_PRIVATE)
        val user = shared.getString("USERNAME", "")
        if(messages[position].login == user) {
            deleteMessageOnServer(messages[position].id)
            messages.removeAt(position)
            notifyItemRemoved(position)
            Toast.makeText(context, "Your message has been deleted", Toast.LENGTH_SHORT).show()
        } else {
            val noDeleted = messages[position]
            messages.removeAt(position)
            notifyItemRemoved(position)
            messages.add(position, noDeleted)
            notifyItemInserted(position)
            Toast.makeText(context, "Cannot delete someone's else message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private fun deleteMessageOnServer(id: String) {
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





}