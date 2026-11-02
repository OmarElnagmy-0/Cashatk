package com.example.cashatk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashatk.data.PreferenceHelper

class LoginActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_LOGIN = "mode_login"             // Standard app lock/unlock
        const val MODE_CREATE = "mode_create"           // Create new PIN
        const val MODE_CHANGE_CURRENT = "mode_change"   // Verify current PIN before changing
        const val MODE_DELETE_VERIFY = "mode_delete"    // Verify current PIN before deleting
    }

    private val enteredPin = StringBuilder()
    private var tempCreatedPin: String? = null
    private var currentStep = 0 // 0 = initial entry, 1 = confirm entry (for MODE_CREATE)
    private val maxPinLength = 6

    private lateinit var prefs: PreferenceHelper
    private lateinit var pinBoxes: List<TextView>
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvForgotPin: TextView
    private lateinit var btnBack: ImageView

    private var currentMode: String = MODE_LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = PreferenceHelper(this)
        currentMode = intent.getStringExtra(EXTRA_MODE) ?: MODE_LOGIN

        initViews()
        setupKeypad()
        applyModeHeader()
        updatePinUi()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvLoginTitle)
        tvSubtitle = findViewById(R.id.tvLoginSubtitle)
        tvForgotPin = findViewById(R.id.tvForgotPin)
        btnBack = findViewById(R.id.btnLoginBack)

        pinBoxes = listOf(
            findViewById(R.id.tvPin1),
            findViewById(R.id.tvPin2),
            findViewById(R.id.tvPin3),
            findViewById(R.id.tvPin4),
            findViewById(R.id.tvPin5),
            findViewById(R.id.tvPin6)
        )

        // Show back button only when invoked as a task/settings sub-screen
        if (currentMode != MODE_LOGIN) {
            btnBack.visibility = View.VISIBLE
            btnBack.setOnClickListener { finish() }
            tvForgotPin.visibility = View.GONE
        } else {
            btnBack.visibility = View.GONE
            tvForgotPin.visibility = View.VISIBLE
            tvForgotPin.setOnClickListener {
                Toast.makeText(this, "Reset PIN via Login & Security in Profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyModeHeader() {
        when (currentMode) {
            MODE_LOGIN -> {
                tvTitle.setText(R.string.login_title)
                tvSubtitle.setText(R.string.login_subtitle)
            }
            MODE_CREATE -> {
                if (currentStep == 0) {
                    tvTitle.setText(R.string.create_pin_title)
                    tvSubtitle.setText(R.string.create_pin_subtitle)
                } else {
                    tvTitle.setText(R.string.confirm_pin_title)
                    tvSubtitle.setText(R.string.confirm_pin_subtitle)
                }
            }
            MODE_CHANGE_CURRENT -> {
                tvTitle.setText(R.string.change_pin_title)
                tvSubtitle.setText(R.string.change_pin_subtitle)
            }
            MODE_DELETE_VERIFY -> {
                tvTitle.setText(R.string.verify_pin_title)
                tvSubtitle.setText(R.string.verify_pin_subtitle)
            }
        }
    }

    private fun setupKeypad() {
        val numberButtons = mapOf(
            R.id.btnKey0 to "0",
            R.id.btnKey1 to "1",
            R.id.btnKey2 to "2",
            R.id.btnKey3 to "3",
            R.id.btnKey4 to "4",
            R.id.btnKey5 to "5",
            R.id.btnKey6 to "6",
            R.id.btnKey7 to "7",
            R.id.btnKey8 to "8",
            R.id.btnKey9 to "9"
        )

        for ((id, digit) in numberButtons) {
            findViewById<TextView>(id)?.setOnClickListener {
                onDigitPressed(digit)
            }
        }

        findViewById<FrameLayout>(R.id.btnKeyBackspace)?.setOnClickListener {
            onBackspacePressed()
        }
    }

    private fun onDigitPressed(digit: String) {
        if (enteredPin.length < maxPinLength) {
            enteredPin.append(digit)
            updatePinUi()
            if (enteredPin.length == maxPinLength) {
                onPinComplete(enteredPin.toString())
            }
        }
    }

    private fun onBackspacePressed() {
        if (enteredPin.isNotEmpty()) {
            enteredPin.deleteCharAt(enteredPin.length - 1)
            updatePinUi()
        }
    }

    private fun updatePinUi() {
        for (i in 0 until maxPinLength) {
            if (i < enteredPin.length) {
                pinBoxes[i].text = "•"
                pinBoxes[i].isActivated = true
            } else {
                pinBoxes[i].text = ""
                pinBoxes[i].isActivated = false
            }
        }
    }

    private fun clearPinInput() {
        enteredPin.clear()
        updatePinUi()
    }

    private fun onPinComplete(pin: String) {
        when (currentMode) {
            MODE_LOGIN -> handleLoginPin(pin)
            MODE_CREATE -> handleCreatePin(pin)
            MODE_CHANGE_CURRENT -> handleChangeVerifyPin(pin)
            MODE_DELETE_VERIFY -> handleDeleteVerifyPin(pin)
        }
    }

    private fun handleLoginPin(pin: String) {
        val savedPin = prefs.getPinCode()
        if (savedPin == null || savedPin == pin) {
            prefs.setLoggedIn(true)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, getString(R.string.pin_incorrect), Toast.LENGTH_SHORT).show()
            clearPinInput()
        }
    }

    private fun handleCreatePin(pin: String) {
        if (currentStep == 0) {
            tempCreatedPin = pin
            currentStep = 1
            clearPinInput()
            applyModeHeader()
        } else {
            if (tempCreatedPin == pin) {
                prefs.savePinCode(pin)
                Toast.makeText(this, getString(R.string.pin_saved_success), Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.pin_mismatch), Toast.LENGTH_SHORT).show()
                currentStep = 0
                tempCreatedPin = null
                clearPinInput()
                applyModeHeader()
            }
        }
    }

    private fun handleChangeVerifyPin(pin: String) {
        val savedPin = prefs.getPinCode()
        if (savedPin == pin) {
            // Current PIN verified, switch to create flow for new PIN
            currentMode = MODE_CREATE
            currentStep = 0
            tempCreatedPin = null
            clearPinInput()
            applyModeHeader()
        } else {
            Toast.makeText(this, getString(R.string.pin_incorrect), Toast.LENGTH_SHORT).show()
            clearPinInput()
        }
    }

    private fun handleDeleteVerifyPin(pin: String) {
        val savedPin = prefs.getPinCode()
        if (savedPin == pin) {
            prefs.savePinCode("") // Clears PIN
            Toast.makeText(this, getString(R.string.pin_removed_success), Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.pin_incorrect), Toast.LENGTH_SHORT).show()
            clearPinInput()
        }
    }
}
