package com.example.shoutboxapp3

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var mAdapter: MessageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager


    private lateinit var username: String
    private lateinit var sendButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkConnection(this).checkNetwork(this, this)

        swipeLayout = findViewById(R.id.swipeRefreshLayout)
        swipeLayout.setOnRefreshListener(this::getDataJson)
        refreshEveryMinute()

        val intentReceived = intent
        username = intentReceived.getStringExtra("USERNAME_HOME").toString()


        val content = findViewById<EditText>(R.id.contentEditText)
        content.hint = "Content"


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this@MainActivity)
        getDataJson()
        recyclerView.layoutManager = linearLayoutManager


        val swipeHandler = object : SwipeToDeleteCallback(this@MainActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView.adapter as MessageAdapter
                adapter.removeAt(viewHolder.bindingAdapterPosition, this@MainActivity)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        val toolbar: Toolbar = findViewById(R.id.toolbar)

//        val customFont = Typeface.createFromAsset(this.assets, "font/comfortaa_variablefont_wght.ttf")
//        val textToolbar = findViewById<TextView>(R.id.custom_title)
//        textToolbar.typeface = customFont
        setSupportActionBar(toolbar)


        drawer = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)


        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        sendButton = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            if (content.text.isNotEmpty()) {
                sendDataToServer()
                content.text.clear()
            }
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_shoutbox -> drawer.closeDrawer(GravityCompat.START)
            R.id.nav_home -> finish()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun getDataJson() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://tgryl.pl/shoutbox/")
            .build()
            .create(MessageApi::class.java)

        retrofitBuilder.getMessages().enqueue(object : Callback<MutableList<MessageModel>?> {
            override fun onResponse(
                call: Call<MutableList<MessageModel>?>,
                response: Response<MutableList<MessageModel>?>
            ) {
                val responseBody = response.body()!!
                val position = responseBody.size
                mAdapter = MessageAdapter(responseBody)
                mAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(position - 1)
                recyclerView.adapter = mAdapter
            }

            override fun onFailure(call: Call<MutableList<MessageModel>?>, t: Throwable) {
                Log.d("onFailureReceive", "Failed when receiving" + t.message)
            }
        })
        swipeLayout.isRefreshing = false
    }

    private fun sendDataToServer() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://tgryl.pl/shoutbox/")
            .build()
            .create(MessageApi::class.java)

        val contentView = findViewById<EditText>(R.id.contentEditText)

        retrofitBuilder.sendMessage(contentView.text.toString(), username).enqueue(
            object : Callback<MessageModel> {
                override fun onResponse(
                    call: Call<MessageModel>,
                    response: Response<MessageModel>
                ) {
                    Log.d("onResponseSend", "Sent")
                    getDataJson()
                }

                override fun onFailure(call: Call<MessageModel>, t: Throwable) {
                    Log.d("onFailureSend", "Failed when sending" + t.message)
                }

            })

    }

    private fun refreshEveryMinute() {
        getDataJson()
        Handler(Looper.getMainLooper()).postDelayed(this::refreshEveryMinute, 60000)
    }


    override fun onResume() {
        super.onResume()
        getDataJson()
    }


}