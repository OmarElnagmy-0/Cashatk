package com.example.cashatk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashatk.data.PreferenceHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var prefs: PreferenceHelper
    private lateinit var profileImage: ShapeableImageView
    private lateinit var profileName: TextView

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@registerForActivityResult

        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            prefs.saveProfileImageUri(uri.toString())
            showProfileImage(uri)
        } catch (_: SecurityException) {
            showProfileImage(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        prefs = PreferenceHelper(this)
        profileImage = findViewById(R.id.iv_profile_image)
        profileName = findViewById(R.id.tv_profile_name)

        setupProfile()
        setupMenu()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        if (::prefs.isInitialized) {
            setupProfile()
        }
    }

    private fun setupProfile() {
        profileName.text = prefs.getUserName().ifEmpty { getString(R.string.profile_default_name) }

        val imageUri = prefs.getProfileImageUri()
        if (imageUri.isNullOrEmpty()) {
            profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        } else {
            showProfileImage(imageUri.toUri())
        }

        val openImagePicker = {
            imagePicker.launch(arrayOf("image/*"))
        }
        profileImage.setOnClickListener { openImagePicker() }
        findViewById<MaterialButton>(R.id.btn_change_photo).setOnClickListener { openImagePicker() }
    }

    private fun showProfileImage(uri: Uri) {
        try {
            checkNotNull(contentResolver.openAssetFileDescriptor(uri, "r")).use { }
            profileImage.setImageURI(uri)
        } catch (_: Exception) {
            prefs.clearProfileImageUri()
            profileImage.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun setupMenu() {
        findViewById<android.view.View>(R.id.menu_categories).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }
        findViewById<android.view.View>(R.id.menu_login_security).setOnClickListener {
            startActivity(Intent(this, LoginSecurityActivity::class.java))
        }
        findViewById<android.view.View>(R.id.menu_data_privacy).setOnClickListener {
            startActivity(Intent(this, DataPrivacyActivity::class.java))
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_stats, R.id.nav_wallet -> {
                    Toast.makeText(this, getString(R.string.coming_phase_2), Toast.LENGTH_SHORT).show()
                    false
                }
                else -> false
            }
        }

        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }
}

