package com.example.cashatk

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cashatk.data.AppDatabase
import com.example.cashatk.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabasePrepopulateCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            prepopulateCategories(context)
        }
    }

    companion object {
        suspend fun prepopulateCategories(context: Context) {
            val database = AppDatabase.getInstance(context)
            val categoryDao = database.categoryDao()

            val existing = categoryDao.getAllCategories()
            if (existing.isEmpty()) {
                CategoryIconHelper.PREDEFINED_CATEGORIES.forEach { item ->
                    categoryDao.insertCategory(
                        Category(
                            name = item.defaultTitle,
                            icon = item.iconName
                        )
                    )
                }
            }
        }
    }
}
