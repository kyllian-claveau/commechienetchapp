package com.example.ccchapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccchapp.databinding.ActivityModifyUserBinding
import com.example.ccchapp.databinding.ActivityUserInformationBinding
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class ModifyUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText

    private lateinit var name: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val refreshButton = findViewById<ImageButton>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            recreate()
        }

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)

        // Get user information from intent
        val userName = intent.getStringExtra("userName")
        val userEmail = intent.getStringExtra("userEmail")

        // Set user information to the EditTexts
        nameEditText.setText(userName)
        emailEditText.setText(userEmail)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            name = nameEditText.text.toString()
            email = emailEditText.text.toString()

            // Send user information to the server using OkHttp
            val sharedPreferences = getSharedPreferences("authentication", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", "")

            if (token != null) {
                val client = OkHttpClient()

                val json = JSONObject()
                json.put("name", name)
                json.put("email", email)

                val requestBody = json.toString().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("http://comme-chien-et-chat.eu/mobile/modify/user-information")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", token)
                    .put(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "User information saved successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Failed to save user information", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "Failed to save user information", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                Log.e("DEBUG", "Token is null.")
            }
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
