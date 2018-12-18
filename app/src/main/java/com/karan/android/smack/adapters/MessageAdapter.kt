package com.karan.android.smack.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.karan.android.smack.R
import com.karan.android.smack.model.Message
import com.karan.android.smack.services.MessageService
import com.karan.android.smack.services.UserDataService

class MessageAdapter(val context: Context, val messages: ArrayList<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val messageUserName = itemView?.findViewById<TextView>(R.id.messageUserName)
        val messageUserImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val messageTimeStamp = itemView?.findViewById<TextView>(R.id.messageTimeStamp)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBody)

        fun bindMessage(position: Int){
            val message = messages[position]
            messageUserName?.text = message.userName
            messageBody?.text = message.msgBody
            messageTimeStamp?.text = message.timeStamp
            messageUserImage?.setImageResource(context.resources.getIdentifier(message.userAvatar,"drawable",context.packageName))
            messageUserImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
        }
    }
}