package com.karan.android.smack.services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.karan.android.smack.controller.App
import com.karan.android.smack.utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun registerUser(email: String, password: String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER,Response.Listener {response ->
            println(response)
            complete(true)
        },Response.ErrorListener { error ->
            Log.d("Error:","Couldn't register the user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(email: String,password: String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN,null,Response.Listener {response ->

            try {
                App.prefs.authToken = response.getString("token")
                App.prefs.userEmail = response.getString("user")
                App.prefs.isLoggedIn = true
                complete(true)

            }catch (e: JSONException){
                Log.d("Error","EXC:" + e.localizedMessage)
                complete(false)
            }


        },Response.ErrorListener {error ->
            Log.d("Error:","Couldn't register the user: $error")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser(name: String,email: String,avatarName: String,avatarColor: String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("name",name)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_ADD_USER,null,Response.Listener {response ->
            try {
                UserDataService.email = response.getString("email")
                UserDataService.name = response.getString("name")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.id = response.getString("_id")
                complete(true)

            }catch (e: JSONException){
                Log.d("Error","EXC: " + e.localizedMessage)
                complete(false)
            }

        },Response.ErrorListener {error ->
            Log.d("Error:","Couldn't register the user: $error")
            complete(false)
        }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(createRequest)
    }

    fun findUserByEmail(context: Context,complete: (Boolean) -> Unit){
        val url = URL_GET_USER + App.prefs.userEmail

        val getUser = object : JsonObjectRequest(Method.GET,url,null,Response.Listener {response ->

            try{
                UserDataService.name = response.getString("name")
                UserDataService.email = response.getString("email")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.id = response.getString("_id")

                val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChanged)

                complete(true)
            }catch (e: JSONException){
                Log.e("Error","ESC: ${e.localizedMessage}")
                complete(false)
            }


        },Response.ErrorListener {error ->
            Log.e("Error","Couldn't find the user: $error")
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
        App.prefs.requestQueue.add(getUser)
    }
}