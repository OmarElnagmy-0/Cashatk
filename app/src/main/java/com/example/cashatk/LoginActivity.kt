package com.example.cashatk

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private val enteredPin = StringBuilder()
    private val maxPinLength = 6
    private lateinit var pinBoxes: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pinBoxes = listOf(
            findViewById(R.id.tvPin1),
            findViewById(R.id.tvPin2),
            findViewById(R.id.tvPin3),
            findViewById(R.id.tvPin4),
            findViewById(R.id.tvPin5),
            findViewById(R.id.tvPin6)
        )

        setupKeypad()
        updatePinUi()
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

        findViewById<TextView>(R.id.tvForgotPin)?.setOnClickListener {
            Toast.makeText(this, "Reset PIN via Security settings", Toast.LENGTH_SHORT).show()
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

    private fun onPinComplete(pin: String) {
        // Ready for validation against PreferenceHelper or AppDatabase
        Toast.makeText(this, "PIN Entered: $pin", Toast.LENGTH_SHORT).show()
    }
}
