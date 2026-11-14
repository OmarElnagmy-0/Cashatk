package com.example.cashatk.utils

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.cashatk.R

object CategoryIconHelper {

    data class CategoryIconItem(
        val iconName: String,
        val defaultTitle: String
    )

    val PREDEFINED_CATEGORIES = listOf(
        CategoryIconItem("ic_cat_food", "Food & Dining"),
        CategoryIconItem("ic_cat_cafe", "Coffee & Drinks"),
        CategoryIconItem("ic_cat_shopping", "Shopping"),
        CategoryIconItem("ic_cat_bills", "Bills & Utilities"),
        CategoryIconItem("ic_cat_transport", "Transportation"),
        CategoryIconItem("ic_cat_travel", "Travel & Trips"),
        CategoryIconItem("ic_cat_entertainment", "Entertainment"),
        CategoryIconItem("ic_cat_gaming", "Gaming & Hobbies"),
        CategoryIconItem("ic_cat_health", "Health & Medical"),
        CategoryIconItem("ic_cat_fitness", "Fitness & Sports"),
        CategoryIconItem("ic_cat_education", "Education"),
        CategoryIconItem("ic_cat_salary", "Salary & Income"),
        CategoryIconItem("ic_cat_gift", "Gifts & Donations"),
        CategoryIconItem("ic_cat_invest", "Investments"),
        CategoryIconItem("ic_cat_pets", "Pets"),
        CategoryIconItem("ic_cat_general", "General / Other")
    )

    val AVAILABLE_ICONS: List<String> = PREDEFINED_CATEGORIES.map { it.iconName }

    fun getDefaultTitleForIcon(iconName: String): String {
        return PREDEFINED_CATEGORIES.firstOrNull { it.iconName == iconName }?.defaultTitle ?: "Other"
    }

    @DrawableRes
    fun getDrawableRes(context: Context, iconName: String?): Int {
        if (iconName.isNullOrEmpty()) return R.drawable.ic_cat_general
        val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.ic_cat_general
    }
}
