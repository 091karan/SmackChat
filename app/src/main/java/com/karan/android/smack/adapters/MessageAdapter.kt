package com.karan.android.smack.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.karan.android.smack.R
import com.karan.android.smack.model.Message
import com.karan.android.smack.services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

        private val messageUserName = itemView.findViewById<TextView>(R.id.messageUserName)
        private val messageUserImage = itemView.findViewById<ImageView>(R.id.messageUserImage)
        private val messageTimeStamp = itemView.findViewById<TextView>(R.id.messageTimeStamp)
        private val messageBody = itemView.findViewById<TextView>(R.id.messageBody)

        fun bindMessage(position: Int){
            val message = messages[position]
            messageUserName?.text = message.userName
            messageBody?.text = message.msgBody
            messageTimeStamp?.text = returnDateString(message.timeStamp)
            messageUserImage?.setImageResource(context.resources.getIdentifier(message.userAvatar,"drawable",context.packageName))
            messageUserImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
        }

        private fun returnDateString(isoString: String): String{
            // 2018-12-18T16:08:54.858Z

            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var convertedDate = Date()
            try{
                convertedDate = isoFormatter.parse(isoString)
            } catch (e: ParseException){
                Log.d("PARSE","Cannot parse date")
            }
            val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)
        }
    }
}