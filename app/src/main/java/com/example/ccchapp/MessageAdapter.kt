package com.example.ccchapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MessageAdapter(private val messages: ArrayList<JSONObject>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var userRole: String? = null

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

        if (message.getBoolean("sent_by_current_user")) {
            holder.messageContentTextView.setBackgroundResource(R.drawable.my_message_background)
        } else {
            holder.messageContentTextView.setBackgroundResource(R.drawable.your_message_background)
            holder.messageContentTextView.layoutParams = (holder.messageContentTextView.layoutParams as RelativeLayout.LayoutParams).apply {
                removeRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.ALIGN_PARENT_START)
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: JSONObject) {
        messages.add(message)
    }

    fun setUserRole(role: String?) {
        userRole = role
    }
}
