package com.example.cashatk

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.data.AppDatabase
import com.example.cashatk.data.PreferenceHelper
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
        loadData()
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
            val transactions = db.transactionDao().getAllTransactionsWithCategory()
            adapter.submitList(transactions)

            val totalExpenses = transactions.sumOf { it.transaction.amount }

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
