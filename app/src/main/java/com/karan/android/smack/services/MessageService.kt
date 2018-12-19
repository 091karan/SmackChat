package com.karan.android.smack.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.karan.android.smack.controller.App
import com.karan.android.smack.model.Channel
import com.karan.android.smack.model.Message
import com.karan.android.smack.utilities.URL_GET_ALL_MSGS
import com.karan.android.smack.utilities.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {

    var channelList = arrayListOf<Channel>()
    var messageList = arrayListOf<Message>()

    fun getChannels(complete: (Boolean) -> Unit){

        val getChannelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->
            try{
                for(x in 0 until response.length()){
                val channel = response.getJSONObject(x)
                val channelName = channel.getString("name")
                val channelDesc = channel.getString("description")
                val channelId = channel.getString("_id")

                val newChannel = Channel(channelName,channelDesc,channelId)
                this.channelList.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException){
                Log.d("Error","EXC: " + e.localizedMessage)
                complete(false)
            }
        }, Response.ErrorListener { error ->
            Log.d("Error","Couldn't get the channels: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(getChannelRequest)
    }

    fun getMessages(channelId: String,complete: (Boolean) -> Unit){

        val url = "$URL_GET_ALL_MSGS$channelId"

        val getMsgsRequest = object : JsonArrayRequest(Method.GET,url,null,Response.Listener { response ->
            clearMessages()
            try {
                for(x in 0 until response.length()){
                    val message = response.getJSONObject(x)

                    val messageBody = message.getString("messageBody")
                    val id = message.getString("_id")
                    val channelId = message.getString("channelId")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMsg = Message(messageBody,channelId,userName,userAvatar,userAvatarColor,id,timeStamp)
                    this.messageList.add(newMsg)

                }
                complete(true)

            }catch (e: JSONException){
                complete(false)
            }


        },Response.ErrorListener {error ->
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(getMsgsRequest)
    }

    fun clearMessages(){
        messageList.clear()
    }

    fun clearChannels(){
        channelList.clear()
    }
}