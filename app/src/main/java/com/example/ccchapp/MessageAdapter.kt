package com.example.ccchapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import okhttp3.internal.notifyAll
import org.json.JSONObject

class MessageAdapter(private val messages: ArrayList<JSONObject>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageContentTextView: TextView = itemView.findViewById(R.id.chat_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageContentTextView.text = message.getString("message")

        if (message.getString("sender_type")!="VENDOR" /* replace by user role */) {
            holder.messageContentTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: JSONObject) {
        messages.add(message)
    }
}