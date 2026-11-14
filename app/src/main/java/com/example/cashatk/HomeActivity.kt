package com.example.cashatk

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.adapters.TransactionAdapter
import com.example.cashatk.data.AppDatabase
import com.example.cashatk.data.PreferenceHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private lateinit var prefs: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        db = AppDatabase.getInstance(this)
        prefs = PreferenceHelper(this)

        setupGreeting()
        setupRecyclerView()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_home -> true
                R.id.nav_stats, R.id.nav_wallet -> {
                    Toast.makeText(this, "Coming in Phase 2", Toast.LENGTH_SHORT).show()
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

    private fun setupGreeting() {
        val tvName = findViewById<TextView>(R.id.tv_name)
        val userName = prefs.getUserName()
        tvName.text = userName.ifEmpty { "User" }
    }

    private fun setupRecyclerView() {
        val rvTransactions = findViewById<RecyclerView>(R.id.rv_transactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter()
        rvTransactions.adapter = adapter
    }

    private fun loadData() {
        lifecycleScope.launch {
            val transactions = db.transactionDao().getAllTransactions()
            val categories = db.categoryDao().getAllCategories()
            adapter.submitData(transactions, categories)

            val totalExpenses = transactions.sumOf { it.amount }

            val format = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
            val formattedTotal = format.format(totalExpenses)

            val tvTotalExpenses = findViewById<TextView>(R.id.tv_total_expenses)
            tvTotalExpenses.text = getString(R.string.total_expenses_format, formattedTotal)
        }
    }
}
