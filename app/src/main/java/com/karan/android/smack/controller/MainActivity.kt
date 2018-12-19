package com.karan.android.smack.controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.karan.android.smack.R
import com.karan.android.smack.adapters.MessageAdapter
import com.karan.android.smack.model.Channel
import com.karan.android.smack.model.Message
import com.karan.android.smack.services.AuthService
import com.karan.android.smack.services.MessageService
import com.karan.android.smack.services.UserDataService
import com.karan.android.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.karan.android.smack.utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter : ArrayAdapter<Channel>
    private lateinit var messageAdapter: MessageAdapter
    var selectedChannel: Channel? = null

    private fun setUpAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channelList)
        channelList.adapter = channelAdapter

        messageAdapter = MessageAdapter(this,MessageService.messageList)
        messageListView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        messageListView.layoutManager = layoutManager

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated",onNewChannel)
        socket.on("messageCreated",onNewMesage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setUpAdapter()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))

        channelList.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channelList[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }

        userNameNavHeader.text = ""
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userEmailNavHeader.text = ""
        loginBtnNavHeader.text = "Login"
    }


    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }


    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {

            if(App.prefs.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                userImageNavHeader.setImageResource(resources.getIdentifier(UserDataService.avatarName,"drawable",packageName))
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = "Log out"

                MessageService.getChannels{complete ->
                    if(complete){
                        if(MessageService.channelList.count() > 0){
                            selectedChannel = MessageService.channelList[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        val channelName = "#${selectedChannel?.name}"
        mainChannelName.text = channelName
        // Pull down the messages which are specific to the channel
        if (selectedChannel != null){
            MessageService.getMessages(selectedChannel!!.id){complete ->
                if(complete){
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0){
                        messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavClicked(view: View){
        if(App.prefs.isLoggedIn){
            //Log out
            UserDataService.logout()
            messageAdapter.notifyDataSetChanged()
            channelAdapter.notifyDataSetChanged()
            userEmailNavHeader.text = ""
            userNameNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text = "Log In"
            mainChannelName.text = "Please Log In"
        }
        else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun addChannelClicked(view: View){
        if(App.prefs.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog,null)
            builder.setView(dialogView)
                .setPositiveButton("Add"){ _, _ ->
                    val nameTxtField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTxtField = dialogView.findViewById<EditText>(R.id.addChannelDescriptionTxt)
                    val channelName = nameTxtField.text.toString()
                    val channelDesc = descTxtField.text.toString()

                    //Create channel with the channel name and description
                    socket.emit("newChannel",channelName,channelDesc)
                }
                .setNegativeButton("Cancel"){ _, _ ->
                }
                .show()

        }
    }

    fun sendMsgBtnClicked(view: View){
        if(App.prefs.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel != null){
            socket.emit("newMessage",
                messageTextField.text.toString(),
                UserDataService.id,
                selectedChannel?.id,
                UserDataService.name,
                UserDataService.avatarName,
                UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn){
            runOnUiThread {
                val channelName = args[0] as String
                val channelDesc = args[1] as String
                val channelId = args[2] as String
                val newChannel = Channel(channelName,channelDesc,channelId)
                MessageService.channelList.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMesage = Emitter.Listener { args ->
        if(App.prefs.isLoggedIn){
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id){
                    val msgBody = args[0] as String
                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMsg = Message(msgBody,channelId,userName,userAvatar,userAvatarColor,id,timeStamp)
                    MessageService.messageList.add(newMsg)
                    messageAdapter.notifyDataSetChanged()
                    messageListView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

}

