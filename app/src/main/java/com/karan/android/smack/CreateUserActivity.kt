package com.karan.android.smack

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun createUserCreateUserBtnClicked(view: View){

    }

    fun generateBackgroundColor(view: View){
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createUserUserAvatar.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble()/255
        val savedG = g.toDouble()/255
        val savedB = b.toDouble()/255

        avatarColor = "[$savedR,$savedG,$savedB,1]"

    }

    fun generateUserAvatar(view: View){
        val random = Random()
        var color = random.nextInt(2)
        var avatar = random.nextInt(28)

        if(color==0){
            userAvatar = "light$avatar"
        }
        else{
            userAvatar = "dark$avatar"
        }
        createUserUserAvatar.setImageResource(this.resources.getIdentifier(userAvatar,"drawable",this.packageName))
    }
}
