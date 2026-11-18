package com.example.cashatk

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.adapters.CategoryAdapter
import com.example.cashatk.data.AppDatabase
import com.example.cashatk.data.Category
import com.example.cashatk.dialogs.CategoryCrudDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: CategoryAdapter
    private var categoryList: List<Category> = emptyList()

    private lateinit var rvCategories: RecyclerView
    private lateinit var layoutEmptyState: View
    private lateinit var tvCategoryCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getInstance(this)

        initViews()
        setupRecyclerView()
        loadCategories()
    }

    private fun initViews() {
        rvCategories = findViewById(R.id.rvCategories)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        tvCategoryCount = findViewById(R.id.tvCategoryCount)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val fabAddCategory = findViewById<ExtendedFloatingActionButton>(R.id.fabAddCategory)
        fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            onEditClick = { category -> showEditCategoryDialog(category) },
            onDeleteClick = { category -> showDeleteConfirmationDialog(category) }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            categoryList = db.categoryDao().getAllCategories()
            adapter.submitList(categoryList)

            val count = categoryList.size
            tvCategoryCount.text = if (count == 1) {
                getString(R.string.category_count_single)
            } else {
                getString(R.string.category_count_plural, count)
            }

            if (categoryList.isEmpty()) {
                layoutEmptyState.visibility = View.VISIBLE
                rvCategories.visibility = View.GONE
            } else {
                layoutEmptyState.visibility = View.GONE
                rvCategories.visibility = View.VISIBLE
            }
        }
    }

    private fun showAddCategoryDialog() {
        CategoryCrudDialog(
            context = this,
            existingCategories = categoryList,
            onSave = { name, icon ->
                lifecycleScope.launch {
                    try {
                        db.categoryDao().insertCategory(Category(name = name, icon = icon))
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_added_success), Toast.LENGTH_SHORT).show()
                        loadCategories()
                    } catch (e: Exception) {
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_add_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ).show()
    }

    private fun showEditCategoryDialog(category: Category) {
        CategoryCrudDialog(
            context = this,
            categoryToEdit = category,
            existingCategories = categoryList,
            onSave = { name, icon ->
                lifecycleScope.launch {
                    try {
                        db.categoryDao().updateCategory(category.copy(name = name, icon = icon))
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_updated_success), Toast.LENGTH_SHORT).show()
                        loadCategories()
                    } catch (e: Exception) {
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_update_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ).show()
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_category)
            .setMessage(getString(R.string.delete_category_confirm, category.name))
            .setNegativeButton(R.string.cancel_btn, null)
            .setPositiveButton(R.string.delete_btn) { _, _ ->
                lifecycleScope.launch {
                    try {
                        db.categoryDao().deleteCategory(category)
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_deleted_success), Toast.LENGTH_SHORT).show()
                        loadCategories()
                    } catch (e: Exception) {
                        Toast.makeText(this@CategoriesActivity, getString(R.string.category_delete_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
