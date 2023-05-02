package com.example.ccchapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccchapp.databinding.ActivityConversationBinding
import com.example.ccchapp.databinding.ActivityHomeBinding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.conversationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val url = "http://comme-chien-et-chat.eu/mobile/usersorvendors/list"

        if (token != null) {
            url.httpGet()
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .response { _, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            Log.e("DEBUG", "HTTP Error: ${result.error.message}")
                            Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            val json = response.data.toString(Charsets.UTF_8)
                            Log.d("DEBUG", "JSON Response: $json")

                            val usersOrVendorsList = mutableListOf<JSONObject>()
                            val jsonArray = JSONObject(json).getJSONArray("usersOrVendors")
                            for (i in 0 until jsonArray.length()) {
                                usersOrVendorsList.add(jsonArray.getJSONObject(i))
                            }

                            conversationAdapter = ConversationAdapter(usersOrVendorsList)
                            recyclerView.adapter = conversationAdapter
                        }
                    }
                }
        } else {
            Log.e("DEBUG", "Token is null.")
        }
        // Click
        binding.downmenuLayout.homeLogout.setOnClickListener {
            auth.signOut()
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "Déconnexion Réussie !", Toast.LENGTH_SHORT).show()
            }
        }
        // Click
        binding.downmenuLayout.homeChat.setOnClickListener {
            startActivity(Intent(this,  ConversationActivity::class.java))
        }
        // Click
        binding.downmenuLayout.button1.setOnClickListener {
            startActivity(Intent(this, ConversationActivity::class.java))
        }
    }
}