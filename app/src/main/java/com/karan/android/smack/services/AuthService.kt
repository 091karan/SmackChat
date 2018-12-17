package com.karan.android.smack.services

import android.content.Context
import android.content.Intent
import android.service.autofill.UserData
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.karan.android.smack.R.string.password
import com.karan.android.smack.utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    fun registerUser(context: Context,email: String, password: String,complete: (Boolean) -> Unit){

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

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context,email: String,password: String,complete: (Boolean) -> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN,null,Response.Listener {response ->

            try {
                authToken = response.getString("token")
                userEmail = response.getString("user")
                isLoggedIn = true
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

        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context,name: String,email: String,avatarName: String,avatarColor: String,complete: (Boolean) -> Unit){

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
                headers.put("Authorization","Bearer $authToken")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(createRequest)
    }

    fun findUserByEmail(context: Context,complete: (Boolean) -> Unit){
        val url = URL_GET_USER + userEmail

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
                Log.d("Error","ESC: ${e.localizedMessage}")
                complete(false)
            }


        },Response.ErrorListener {error ->
            Log.d("Error","Couldn't find the user: $error")
            complete(false)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization", authToken)
                return headers
            }
        }
        Volley.newRequestQueue(context).add(getUser)
    }

}