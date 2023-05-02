package com.example.ccchapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class ConversationAdapter(private val usersOrVendors: List<JSONObject>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.conversation_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.conversation_item, parent, false)
        return ConversationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val userOrVendor = usersOrVendors[position]
        holder.nameTextView.text = userOrVendor.getString("name")

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
        return usersOrVendors.size
    }

}
