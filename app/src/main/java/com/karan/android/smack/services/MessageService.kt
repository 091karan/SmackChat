package com.karan.android.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.karan.android.smack.controller.App
import com.karan.android.smack.model.Channel
import com.karan.android.smack.model.Message
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
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(getChannelRequest)
    }

}