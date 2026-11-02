package com.example.cashatk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashatk.data.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            routeUser()
        }, 1200)
    }

    private fun routeUser() {
        val prefs = PreferenceHelper(this)

        if (!prefs.isOnboardingCompleted()) {
            startActivity(Intent(this, OnboardingWelcomeActivity::class.java))
        } else if (prefs.hasPinCode()) {
            // User enabled PIN security -> show Login PIN screen
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra(LoginActivity.EXTRA_MODE, LoginActivity.MODE_LOGIN)
            }
            startActivity(intent)
        } else {
            // User did NOT enable PIN security -> go straight to Home
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish()
    }
}
