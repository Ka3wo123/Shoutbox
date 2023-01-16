package com.example.shoutboxapp3

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface MessageApi {
    @GET("messages?last=10")
    fun getMessages(): Call<MutableList<MessageModel>>

    @FormUrlEncoded
    @POST("message")
    fun sendMessage(@Field("content") content: String, @Field("login") login: String): Call<MessageModel>

    @FormUrlEncoded
    @PUT("message/{id}")
    fun editMessage(
        @Path("id") id: String,
        @Field("content") contentEdit: String,
        @Field("login") loginEdit: String
    ): Call<MessageModel>

    @DELETE("message/{id}")
    fun deleteMessage(@Path("id") id: String) : Call<MessageModel>


}