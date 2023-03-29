package com.example.ccchapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONArray

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
    }

    private fun fetchMessages() {
        val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        val url = "http://comme-chien-et-chat.eu/mobile/messages"
        url.httpGet()
            .header("Content-Type", "application/json")
            .header("Authorization", "token")
            .response { _, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        val json = response.data.toString(Charsets.UTF_8)
                        val jsonArray = JSONArray(json)

                        // Ici, vous pouvez traiter les messages
                        for (i in 0 until jsonArray.length()) {
                            val message = jsonArray.getJSONObject(i)
                            val content = message.getString("messages")

                            // Vous pouvez ajouter le contenu des messages à une liste ou les afficher à l'écran.
                        }
                    }
                }
            }
    }
}