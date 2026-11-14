// package com.example.cashatk

// import android.app.Activity
// import android.content.Intent
// import android.os.Bundle
// import android.view.View
// import android.widget.ImageView
// import android.widget.LinearLayout
// import android.widget.TextView
// import androidx.activity.enableEdgeToEdge
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.appcompat.app.AppCompatActivity
// import androidx.core.content.ContextCompat
// import androidx.core.view.ViewCompat
// import androidx.core.view.WindowInsetsCompat
// import com.example.cashatk.data.PreferenceHelper

// class LoginSecurityActivity : AppCompatActivity() {

//     private lateinit var prefs: PreferenceHelper

//     private lateinit var tvSecurityStatus: TextView
//     private lateinit var tvSecurityDesc: TextView
//     private lateinit var ivSecurityIcon: ImageView

//     private lateinit var cardCreatePin: LinearLayout
//     private lateinit var cardChangePin: LinearLayout
//     private lateinit var cardDeletePin: LinearLayout

//     private val pinLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//         if (result.resultCode == Activity.RESULT_OK) {
//             refreshSecurityUi()
//         }
//     }

//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         enableEdgeToEdge()
//         setContentView(R.layout.activity_login_security)

//         ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//             val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//             v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//             insets
//         }

//         prefs = PreferenceHelper(this)

//         initViews()
//         setupListeners()
//         refreshSecurityUi()
//     }

//     override fun onResume() {
//         super.onResume()
//         refreshSecurityUi()
//     }

//     private fun initViews() {
//         tvSecurityStatus = findViewById(R.id.tvSecurityStatus)
//         tvSecurityDesc = findViewById(R.id.tvSecurityDescription)
//         ivSecurityIcon = findViewById(R.id.ivSecurityIcon)

//         cardCreatePin = findViewById(R.id.cardCreatePin)
//         cardChangePin = findViewById(R.id.cardChangePin)
//         cardDeletePin = findViewById(R.id.cardDeletePin)

//         findViewById<ImageView>(R.id.btnBack).setOnClickListener {
//             finish()
//         }
//     }

//     private fun setupListeners() {
//         // Choice 1: Create PIN (When user doesn't have a PIN)
//         cardCreatePin.setOnClickListener {
//             val intent = Intent(this, LoginActivity::class.java).apply {
//                 putExtra(LoginActivity.EXTRA_MODE, LoginActivity.MODE_CREATE)
//             }
//             pinLauncher.launch(intent)
//         }

//         // Choice 2: Change existing PIN (When user has a PIN)
//         cardChangePin.setOnClickListener {
//             val intent = Intent(this, LoginActivity::class.java).apply {
//                 putExtra(LoginActivity.EXTRA_MODE, LoginActivity.MODE_CHANGE_CURRENT)
//             }
//             pinLauncher.launch(intent)
//         }

//         // Choice 3: Delete / Remove PIN (When user has a PIN)
//         cardDeletePin.setOnClickListener {
//             val intent = Intent(this, LoginActivity::class.java).apply {
//                 putExtra(LoginActivity.EXTRA_MODE, LoginActivity.MODE_DELETE_VERIFY)
//             }
//             pinLauncher.launch(intent)
//         }
//     }

//     private fun refreshSecurityUi() {
//         val hasPin = prefs.hasPinCode()

//         if (hasPin) {
//             // PIN is active: Hide create card, show change & delete options
//             tvSecurityStatus.setText(R.string.security_status_enabled)
//             tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_primary))
//             ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.teal_primary))

//             cardCreatePin.visibility = View.GONE
//             cardChangePin.visibility = View.VISIBLE
//             cardDeletePin.visibility = View.VISIBLE
//         } else {
//             // No PIN: Show create card, hide change & delete options
//             tvSecurityStatus.setText(R.string.security_status_disabled)
//             tvSecurityStatus.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
//             ivSecurityIcon.setColorFilter(ContextCompat.getColor(this, R.color.nav_inactive))

//             cardCreatePin.visibility = View.VISIBLE
//             cardChangePin.visibility = View.GONE
//             cardDeletePin.visibility = View.GONE
//         }
//     }
// }
