package com.example.cashatk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.data.Category

class CategoryAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditCategory)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.tvName.text = category.name

        val drawableRes = CategoryIconHelper.getDrawableRes(holder.itemView.context, category.icon)
        holder.ivIcon.setImageResource(drawableRes)

        holder.btnEdit.setOnClickListener { onEditClick(category) }
        holder.btnDelete.setOnClickListener { onDeleteClick(category) }
    }

    companion object {
        private val CategoryDiffCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem == newItem
        }
    }
}
