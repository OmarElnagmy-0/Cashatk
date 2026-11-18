package com.example.cashatk.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.R
import com.example.cashatk.adapters.IconPickerAdapter
import com.example.cashatk.data.Category
import com.example.cashatk.utils.CategoryIconHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CategoryCrudDialog(
    private val context: Context,
    private val categoryToEdit: Category? = null,
    private val existingCategories: List<Category> = emptyList(),
    private val onSave: (name: String, icon: String) -> Unit
) {
    private var dialog: AlertDialog? = null

    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_category_crud, null)

        val tvTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvDialogSubtitle)
        val rvIcons = view.findViewById<RecyclerView>(R.id.rvIcons)
        val tilName = view.findViewById<TextInputLayout>(R.id.tilCategoryName)
        val etName = view.findViewById<TextInputEditText>(R.id.etCategoryName)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)

        var selectedIcon = categoryToEdit?.icon ?: CategoryIconHelper.AVAILABLE_ICONS.first()
        var userManuallyEditedName = categoryToEdit != null

        if (categoryToEdit != null) {
            tvTitle.text = context.getString(R.string.edit_category)
            tvSubtitle.text = context.getString(R.string.edit_category_subtitle)
            etName.setText(categoryToEdit.name)
        } else {
            tvTitle.text = context.getString(R.string.add_category)
            tvSubtitle.text = context.getString(R.string.add_category_subtitle)
            etName.setText(CategoryIconHelper.getDefaultTitleForIcon(selectedIcon))
        }

        etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                userManuallyEditedName = true
            }
        }

        val iconAdapter = IconPickerAdapter(
            icons = CategoryIconHelper.AVAILABLE_ICONS,
            selectedIcon = selectedIcon
        ) { chosenIcon ->
            selectedIcon = chosenIcon
            if (categoryToEdit == null && !userManuallyEditedName) {
                etName.setText(CategoryIconHelper.getDefaultTitleForIcon(chosenIcon))
            }
        }

        rvIcons.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvIcons.adapter = iconAdapter

        val initialPosition = CategoryIconHelper.AVAILABLE_ICONS.indexOf(selectedIcon)
        if (initialPosition != -1) {
            rvIcons.scrollToPosition(initialPosition)
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        btnSave.setOnClickListener {
            val name = etName.text?.toString()?.trim() ?: ""

            if (name.isEmpty()) {
                tilName.error = context.getString(R.string.error_category_empty)
                return@setOnClickListener
            }

            val isDuplicate = existingCategories.any {
                it.name.equals(name, ignoreCase = true) && it.id != (categoryToEdit?.id ?: -1)
            }

            if (isDuplicate) {
                tilName.error = context.getString(R.string.error_category_duplicate)
                return@setOnClickListener
            }

            tilName.error = null
            onSave(name, selectedIcon)
            dialog?.dismiss()
        }

        dialog = MaterialAlertDialogBuilder(context)
            .setView(view)
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }
}
