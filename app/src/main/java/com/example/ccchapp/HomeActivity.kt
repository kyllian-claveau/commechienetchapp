package com.example.ccchapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.ccchapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

class HomeActivity : AppCompatActivity(){

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        findViewById<AppCompatButton>(R.id.btn_join).setOnClickListener {
            val conferenceName = findViewById<EditText>(R.id.conferenceName).text.toString()
            val jitsiMeetUserInfo = JitsiMeetUserInfo()
            jitsiMeetUserInfo.displayName = "Utilisateur"
            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(URL("https://meet.jit.si"))
                .setRoom(conferenceName)
                .setUserInfo(jitsiMeetUserInfo)
                .build()
            JitsiMeetActivity.launch(this, options)
        }
        // Auth
        auth = FirebaseAuth.getInstance()

        // Click
        binding.downmenuLayout.homeLogout.setOnClickListener{
            auth.signOut()
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "Déconnexion Réussie !", Toast.LENGTH_SHORT).show()
            }
        }
        // Click
        binding.downmenuLayout.homeChat.setOnClickListener{
            Intent(this, MessageActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}