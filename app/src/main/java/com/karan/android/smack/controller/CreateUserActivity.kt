package com.karan.android.smack.controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.karan.android.smack.R
import com.karan.android.smack.services.AuthService
import com.karan.android.smack.utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createUserProgressBar.visibility = View.INVISIBLE
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


    fun createUserCreateUserBtnClicked(view: View){
        enableSpinner(true)
        val userName = createUserUserNameTxt.text.toString()
        val email = createUserEmailTxt.text.toString()
        val password = createUserPasswordTxt.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
            AuthService.registerUser(email,password){regsiterSuccess ->
                if (regsiterSuccess){
                    AuthService.loginUser(email,password){loginSuccess ->
                        if(loginSuccess){
                            AuthService.createUser(userName,email,userAvatar,avatarColor){createSuccess ->
                                if(createSuccess){
                                    val userDataChanged = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChanged)
                                    enableSpinner(false)
                                    finish()
                                } else{
                                    errorToast()
                                }
                            }
                        } else{
                            errorToast()
                        }
                    }
                } else{
                    errorToast()
                }
            }
        }
        else{
            Toast.makeText(this,"Make sure User Name, Email and Pasword are filled in",Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }


    }

    fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again!",Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            createUserProgressBar.visibility = View.VISIBLE
        }
        else{
            createUserProgressBar.visibility = View.INVISIBLE
        }
        createUserGenerateBackgroundColor.isEnabled = !enable
        createUserUserAvatar.isEnabled = !enable
        createUserGenerateBackgroundColor.isEnabled = !enable
    }
}
