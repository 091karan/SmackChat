package com.karan.android.smack.utilities

const val BASE_URL = "https://smack-chatting.herokuapp.com/v1/"
//const val BASE_URL = "http://10.0.2.2:3005/v1/"                       {It is used for local deploy of project}
const val SOCKET_URL = "https://smack-chatting.herokuapp.com/"

const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_ADD_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel"
const val URL_GET_ALL_MSGS = "${BASE_URL}message/byChannel/"

//Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"
