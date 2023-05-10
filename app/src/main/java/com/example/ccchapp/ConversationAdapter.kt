package com.example.ccchapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.dpToPx
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ConversationAdapter(private val usersOrVendors: List<JSONObject>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {
    private var isGrayBackground = false
    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.conversation_list)
        val imageView: ImageView = itemView.findViewById(R.id.conversation_user_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.conversation_item, parent, false)
        return ConversationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val grayColor = ContextCompat.getColor(holder.itemView.context, com.amplitude.R.color.amp_light_gray)
        val whiteColor = ContextCompat.getColor(holder.itemView.context,R.color.white)

        if (isGrayBackground) {
            holder.itemView.setBackgroundColor(grayColor)
        } else {
            holder.itemView.setBackgroundColor(whiteColor)
        }

        isGrayBackground = !isGrayBackground
        val userOrVendor = usersOrVendors[position]
        Log.d("DEBUG", "Liste: $userOrVendor")
        holder.nameTextView.text = userOrVendor.getString("name")
        val imageName = userOrVendor.getString("avatarFileName")
        Log.d("DEBUG", "Image: $imageName")
        val imageUrl = "http://comme-chien-et-chat.eu/avatar/$imageName"
        val imageDefaultUrl = "http://comme-chien-et-chat.eu/avatar/645c0f4e83aee.png"
        val imageSizeInDp = 30
        val imageSizeInPx = dpToPx(holder.itemView.context, imageSizeInDp)
        if (imageName == "null") {
            Picasso.get()
                .load(imageDefaultUrl) // Image par défaut
                .resize(imageSizeInPx, imageSizeInPx)
                .centerCrop()
                .into(holder.imageView)
        } else {
            Picasso.get()
                .load(imageUrl)
                .resize(imageSizeInPx, imageSizeInPx)
                .centerCrop()
                .into(holder.imageView)
        }
        Log.d("DEBUG", "Image: $imageUrl")
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
    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    override fun getItemCount(): Int {
        return usersOrVendors.size
    }

}