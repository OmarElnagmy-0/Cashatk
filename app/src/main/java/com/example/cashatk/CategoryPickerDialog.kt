package com.example.cashatk

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.data.Category
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CategoryPickerDialog(
    private val context: Context,
    private val categories: List<Category>,
    private val onCategorySelected: (Category) -> Unit,
    private val onManageCategoriesClick: () -> Unit
) {
    private var dialog: AlertDialog? = null

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_category_picker, null)
        val rvCategories = view.findViewById<RecyclerView>(R.id.rvPickerCategories)
        val btnManage = view.findViewById<MaterialButton>(R.id.btnManageCategories)

        val adapter = CategoryPickerAdapter { category ->
            onCategorySelected(category)
            dialog?.dismiss()
        }
        rvCategories.layoutManager = LinearLayoutManager(context)
        rvCategories.adapter = adapter
        adapter.submitList(categories)

        btnManage.setOnClickListener {
            dialog?.dismiss()
            onManageCategoriesClick()
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}
