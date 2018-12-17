package com.karan.android.smack.controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.karan.android.smack.R
import com.karan.android.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableSpinner(false)
    }

    fun loginLoginBtnClicked(view: View){
        enableSpinner(true)
        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        hideKeyboard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(this,email,password){loginSuccess ->
                if(loginSuccess){
                    AuthService.findUserByEmail(this){findUserSuccess ->
                        if(findUserSuccess){
                            finish()
                        } else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
            }
        }
        else{
            Toast.makeText(this,"Please enter the email and password.",Toast.LENGTH_SHORT).show()
            enableSpinner(false)
        }
    }

    private fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again",Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    fun loginCreateUserBtn(view: View){
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            loginProgressBar.visibility = View.VISIBLE
        }else{
            loginProgressBar.visibility = View.INVISIBLE
        }
        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }

    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }

}
