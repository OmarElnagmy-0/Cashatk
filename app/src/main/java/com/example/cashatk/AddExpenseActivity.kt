package com.example.cashatk

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cashatk.data.AppDatabase
import com.example.cashatk.data.Category
import com.example.cashatk.data.Transaction
import com.example.cashatk.dialogs.CategoryPickerDialog
import com.example.cashatk.utils.CategoryIconHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    private var selectedCategory: Category? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getInstance(this)

        setupBackButton()
        setupCategorySelector()
        setupAmountClear()
        setupDateSelector()
        setupAddButton()

        updateDateDisplay()
    }

    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }
    }

    private fun setupCategorySelector() {
        val layoutCategorySelector = findViewById<LinearLayout>(R.id.layoutCategorySelector)
        layoutCategorySelector.setOnClickListener {
            lifecycleScope.launch {
                val categories = db.categoryDao().getAllCategories()
                CategoryPickerDialog(
                    context = this@AddExpenseActivity,
                    categories = categories,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        updateCategoryDisplay(category)
                    },
                    onManageCategoriesClick = {
                        startActivity(Intent(this@AddExpenseActivity, CategoriesActivity::class.java))
                    }
                ).show()
            }
        }
    }

    private fun updateCategoryDisplay(category: Category) {
        val tvCategoryName = findViewById<TextView>(R.id.tvCategoryName)
        val ivCategoryIcon = findViewById<ImageView>(R.id.ivCategoryIcon)

        tvCategoryName.text = category.name
        val iconRes = CategoryIconHelper.getDrawableRes(this, category.icon)
        ivCategoryIcon.setImageResource(iconRes)
    }

    private fun setupAmountClear() {
        val tvClear = findViewById<TextView>(R.id.tvClearAmount)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        tvClear.setOnClickListener {
            etAmount.text.clear()
        }
    }

    private fun setupDateSelector() {
        val layoutDateSelector = findViewById<LinearLayout>(R.id.layoutDateSelector)
        layoutDateSelector.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_label))
                .setSelection(selectedDateMillis)
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                selectedDateMillis = millis
                updateDateDisplay()
            }

            picker.show(supportFragmentManager, "DATE_PICKER")
        }
    }

    private fun updateDateDisplay() {
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        tvDate.text = formatter.format(Date(selectedDateMillis))
    }

    private fun setupAddButton() {
        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val etAmount = findViewById<EditText>(R.id.etAmount)
            val etDescription = findViewById<EditText>(R.id.etDescription)

            val amountText = etAmount.text.toString().trim()
            val amount = amountText.toDoubleOrNull()

            if (selectedCategory == null) {
                Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amount == null || amount <= 0) {
                Toast.makeText(this, getString(R.string.error_valid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                categoryId = selectedCategory!!.id,
                amount = amount,
                description = etDescription.text.toString().trim().ifEmpty { null },
                date = selectedDateMillis
            )

            lifecycleScope.launch {
                try {
                    db.transactionDao().insertTransaction(transaction)
                    Toast.makeText(this@AddExpenseActivity, getString(R.string.expense_added_success), Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@AddExpenseActivity, getString(R.string.expense_add_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
