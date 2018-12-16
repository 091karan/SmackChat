package com.karan.android.smack.services

import android.graphics.Color
import android.text.TextUtils.replace
import java.util.*

object UserDataService {

    var name = ""
    var email = ""
    var avatarName = ""
    var avatarColor = ""
    var id = ""

    fun logout(){
        name = ""
        email = ""
        avatarName = ""
        avatarColor = ""
        id = ""
        AuthService.authToken = ""
        AuthService.isLoggedIn = false
        AuthService.userEmail = ""
    }


    fun returnAvatarColor(components: String): Int{
        // components = [0.00255265266, 0.556562262, 0.64538454554,1]
        val strippedColor = components
                                .replace("[","")
                                .replace("]","")
                                .replace(",","")

        //strippedColor = 0.00255265266 0.556562262 0.64538454554 1

        var r = 0
        var g = 0
        var b = 0

        var scanner = Scanner(strippedColor)
        if(scanner.hasNext()){
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r,g,b)
    }


}