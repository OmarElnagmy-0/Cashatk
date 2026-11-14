package com.example.cashatk.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashatk.R
import com.example.cashatk.utils.CategoryIconHelper

class IconPickerAdapter(
    private val icons: List<String>,
    private var selectedIcon: String,
    private val onIconSelected: (String) -> Unit
) : RecyclerView.Adapter<IconPickerAdapter.IconViewHolder>() {

    class IconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view.findViewById(R.id.layoutIconOption)
        val ivIcon: ImageView = view.findViewById(R.id.ivIconOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon_option, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconName = icons[position]
        val context = holder.itemView.context
        val drawableRes = CategoryIconHelper.getDrawableRes(context, iconName)
        holder.ivIcon.setImageResource(drawableRes)

        val isSelected = iconName.equals(selectedIcon, ignoreCase = true)
        holder.root.isSelected = isSelected

        holder.root.setOnClickListener {
            val previousSelected = selectedIcon
            selectedIcon = iconName
            val prevIndex = icons.indexOf(previousSelected)
            if (prevIndex != -1) notifyItemChanged(prevIndex)
            notifyItemChanged(position)
            onIconSelected(iconName)
        }
    }

    override fun getItemCount(): Int = icons.size

    fun getSelectedIcon(): String = selectedIcon

    fun setSelectedIcon(iconName: String) {
        val prevIndex = icons.indexOf(selectedIcon)
        selectedIcon = iconName
        val newIndex = icons.indexOf(iconName)
        if (prevIndex != -1) notifyItemChanged(prevIndex)
        if (newIndex != -1) notifyItemChanged(newIndex)
    }
}
