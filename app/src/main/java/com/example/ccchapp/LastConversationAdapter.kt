package com.example.ccchapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class LastConversationAdapter(private val conversations: List<JSONObject>, private val userType: String) :
    RecyclerView.Adapter<LastConversationAdapter.LastConversationViewHolder>() {

    class LastConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.last_conversation_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.last_conversation_item, parent, false)
        return LastConversationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LastConversationViewHolder, position: Int) {
        val userOrVendor = conversations[position]

        val name = if (userType == "vendor" && userOrVendor.has("userName")) userOrVendor.getString("userName") else userOrVendor.getString("vendorName")

        holder.nameTextView.text = name

        // Ajouter un OnClickListener à chaque élément de la liste
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // Créer un objet Intent et y ajouter l'identifiant du vendeur ou de l'utilisateur sélectionné
            val intent = Intent(context, MessageActivity::class.java)
            val id = userOrVendor.getInt("id")
            intent.putExtra("id", id)

            // Démarrer l'activité MessageActivity en passant l'objet Intent
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }


}
