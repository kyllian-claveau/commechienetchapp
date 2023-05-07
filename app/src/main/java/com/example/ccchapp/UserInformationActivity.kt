package com.example.ccchapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccchapp.databinding.ActivityUserInformationBinding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class UserInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInformationBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val refreshButton = findViewById<ImageButton>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            recreate()
        }
        val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val url = "http://comme-chien-et-chat.eu/mobile/show/user-information"

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

                            val userInformation = JSONObject(json).getJSONObject("userOrVendorInformation")

                            binding.userNameInformationView.text = userInformation.getString("name")
                            binding.userEmailInformationView.text = userInformation.getString("email")
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
        binding.button.setOnClickListener {
            startActivity(Intent(this, ModifyUserActivity::class.java))
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
}
