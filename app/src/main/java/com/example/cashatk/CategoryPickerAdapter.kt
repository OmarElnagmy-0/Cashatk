package com.example.cashatk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.data.Category

class CategoryPickerAdapter(
    private val onCategorySelected: (Category) -> Unit
) : ListAdapter<Category, CategoryPickerAdapter.PickerViewHolder>(PickerDiffCallback) {

    class PickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvPickerCategoryName)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivPickerCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_picker, parent, false)
        return PickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {
        val category = getItem(position)
        holder.tvName.text = category.name

        val drawableRes = CategoryIconHelper.getDrawableRes(holder.itemView.context, category.icon)
        holder.ivIcon.setImageResource(drawableRes)

        holder.itemView.setOnClickListener {
            onCategorySelected(category)
        }
    }

    companion object {
        private val PickerDiffCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
                oldItem == newItem
        }
    }
}
