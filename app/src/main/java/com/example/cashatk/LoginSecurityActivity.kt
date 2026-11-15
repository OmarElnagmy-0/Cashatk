package com.example.cashatk

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashatk.data.PreferenceHelper
import com.google.android.material.materialswitch.MaterialSwitch

class LoginSecurityActivity : AppCompatActivity() {

    private lateinit var prefs: PreferenceHelper
    private lateinit var tvSecurityStatus: TextView
    private lateinit var ivSecurityIcon: ImageView
    private lateinit var switchAppLock: MaterialSwitch

    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_security)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = PreferenceHelper(this)
        tvSecurityStatus = findViewById(R.id.tvSecurityStatus)
        ivSecurityIcon = findViewById(R.id.ivSecurityIcon)
        switchAppLock = findViewById(R.id.switchAppLock)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        refreshSecurityUi()

        switchAppLock.setOnClickListener {
            val targetEnabled = switchAppLock.isChecked
            switchAppLock.isChecked = !targetEnabled

            if (targetEnabled && !canAuthenticate()) {
                Toast.makeText(this, getString(R.string.app_lock_not_available), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            authenticateUser(
                onSuccess = {
                    prefs.setAppLockEnabled(targetEnabled)
                    refreshSecurityUi()
                }
            )
        }
    }

    private fun canAuthenticate(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun authenticateUser(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@LoginSecurityActivity,
                        getString(R.string.app_lock_auth_failed),
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun refreshSecurityUi() {
        val enabled = prefs.isAppLockEnabled()
        switchAppLock.isChecked = enabled

        if (enabled) {
            tvSecurityStatus.setText(R.string.security_status_enabled)
            tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_primary))
            ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.teal_primary))
        } else {
            tvSecurityStatus.setText(R.string.security_status_disabled)
            tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive))
        }
    }
}
