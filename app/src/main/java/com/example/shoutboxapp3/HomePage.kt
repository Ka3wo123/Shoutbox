package com.example.shoutboxapp3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class HomePage : AppCompatActivity() {
    lateinit var username: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        NetworkConnection(this).checkNetwork(this, this)

        username = findViewById(R.id.loginEditText)
        loadUsername()
        username.hint = "Login"
        val loginButton: Button = findViewById(R.id.setLoginButton)

        loginButton.setOnClickListener {
            if (username.text.isNotEmpty()) {
                saveUsername(username.text.toString().trim())
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USERNAME_HOME", username.text.toString().trim())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loadUsername() {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val u = sharedPreferences.getString("USERNAME", "")

        username.setText(u)
    }

    private fun saveUsername(username: String) {
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("USERNAME", username)
        editor.apply()
    }


}