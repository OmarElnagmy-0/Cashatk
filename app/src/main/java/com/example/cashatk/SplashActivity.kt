package com.example.cashatk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashatk.data.PreferenceHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var prefs: PreferenceHelper

    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = PreferenceHelper(this)

        Handler(Looper.getMainLooper()).postDelayed({
            routeUser()
        }, 1200)
    }

    private fun routeUser() {
        if (!prefs.isOnboardingCompleted()) {
            startActivity(Intent(this, OnboardingWelcomeActivity::class.java))
            finish()
            return
        }

        if (prefs.isAppLockEnabled()) {
            authenticateUser()
        } else {
            openHome()
        }
    }

    private fun authenticateUser() {
        if (BiometricManager.from(this).canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, R.string.app_lock_not_available, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    openHome()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@SplashActivity,
                        R.string.app_lock_auth_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@SplashActivity, errString, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_lock_prompt_title))
            .setSubtitle(getString(R.string.app_lock_prompt_subtitle))
            .setAllowedAuthenticators(authenticators)
            .build()

        prompt.authenticate(promptInfo)
    }

    private fun openHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
