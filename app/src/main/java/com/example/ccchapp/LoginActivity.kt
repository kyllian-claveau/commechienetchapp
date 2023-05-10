package com.example.ccchapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.kittinunf.result.Result;
import androidx.core.content.ContextCompat
import com.example.ccchapp.databinding.ActivityLoginBinding
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import org.json.JSONObject

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Auth
        auth = FirebaseAuth.getInstance()

        // Username Validation
        val usernameStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Votre Email/Identifiant")
        }

        // Password Validation
        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Votre mot de passe")
        }

        // Button Enable or Not
        val invalidFieldsStream = Observable.combineLatest(
            usernameStream,
            passwordStream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnLogin.isEnabled = true
                binding.btnLogin.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary)
            } else {
                binding.btnLogin.isEnabled = false
                binding.btnLogin.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        // Click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            loginUser(email, password)
        }
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Email/Username")
            binding.etEmail.error = if (isNotValid) "$text ne pas pas être vide !" else null
        else if (text == "Password")
            binding.etPassword.error =
                if (isNotValid) "$text ne peut pas être vide !" else null
    }

    private fun loginUser(email: String, password: String) {
        val url = "http://comme-chien-et-chat.eu/mobile/login"
        val credentials = JsonObject()
        credentials.addProperty("email", email)
        credentials.addProperty("password", password)
        url.httpPost()
            .body(credentials.toString())
            .header("Content-Type", "application/json")
            .response { _, response, result ->
                when (result) {
                    is Result.Failure -> {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        val json = response.data.toString(Charsets.UTF_8)
                        val jsonObject = JSONObject(json)
                        val token = jsonObject.getString("token")
                        val sharedPreferences =
                            getSharedPreferences("authentication", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("token", token)
                        editor.apply()
                        Intent(this, HomeActivity::class.java).also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(this, "Connexion Réussie !", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
//        println(response.body())
//                if (login.isSuccessful) {
//                    Intent(this, HomeActivity::class.java).also {
//                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(it)
//                        Toast.makeText(this, "Connexion Réussie !", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this, login.exception?.message, Toast.LENGTH_SHORT).show()
//                }
//            }
    }
}