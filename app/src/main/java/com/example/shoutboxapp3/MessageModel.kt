package com.example.shoutboxapp3

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class MessageModel(
    @SerializedName("content")
    val content: String,
    @SerializedName("login")
    val login: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: String

)
