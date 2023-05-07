package com.example.ccchapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccchapp.databinding.ActivityMessageBinding
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

@Suppress("DEPRECATION")
class MessageActivity : AppCompatActivity() {

    private val REFRESH_DELAY_MS = 10000 // 10 secondes
    private val handler = Handler()
    private lateinit var binding: ActivityMessageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var inputText: EditText
    private lateinit var submitButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val refreshButton = findViewById<ImageButton>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            recreate()
        }

        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        inputText = findViewById(R.id.editText)
        submitButton = findViewById(R.id.sendButton)

        submitButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            val id = intent.getIntExtra("id", -1) // -1 est la valeur par défaut si le paramètre n'est pas trouvé
            val url = "http://comme-chien-et-chat.eu/mobile/messages/create/$id"
            val createMessage = JSONObject()
            createMessage.put("message", inputText.text)
            if (token != null) {
                url.httpPost()
                    .header("Content-Type", "application/json")
                    .header("Authorization", token)
                    .jsonBody(createMessage.toString())
                    .response { _, response, result ->
                        when (result) {
                            is Result.Failure -> {
                                Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                            is Result.Success -> {
                                Toast.makeText(this, "Succès !", Toast.LENGTH_SHORT).show()
                                inputText.text.clear()
                                createMessage.put("sender_type", "")
                                fetchMessages()
                            }
                        }
                    }
            }
        }
        refreshMessages()
        fetchMessages()
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
        binding.downmenuLayout.setting.setOnClickListener {
            startActivity(Intent(this, UserInformationActivity::class.java))
        }
        // Click
        binding.downmenuLayout.home.setOnClickListener {
            startActivity(Intent(this,  HomeActivity::class.java))
        }
        // Click
        binding.downmenuLayout.homeChat.setOnClickListener {
            startActivity(Intent(this,  ConversationActivity::class.java))
        }
    }
    private fun refreshMessages() {
        fetchMessages()
        handler.postDelayed({ refreshMessages() }, REFRESH_DELAY_MS.toLong())
    }
    private fun fetchMessages() {
        val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val id = intent.getIntExtra("id", -1) // -1 est la valeur par défaut si le paramètre n'est pas trouvé
        val url = "http://comme-chien-et-chat.eu/mobile/messages/list/$id"
        if (token != null) {
            url.httpGet()
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .response { _, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                        }

                        is Result.Success -> {
                            val json = response.data.toString(Charsets.UTF_8)
                            val messages = JSONObject(json).getJSONArray("messages")
                            val messageListJsonObject = ArrayList<JSONObject>()
                            for (i in 0 until messages.length()) {
                                messageListJsonObject.add(messages.getJSONObject(i))
                            }
                            messageAdapter = MessageAdapter(messageListJsonObject)
                            messageRecyclerView.adapter = messageAdapter
                            messageRecyclerView.scrollToPosition(messages.length()-1)
                        }
                    }
                }
        }
    }

}