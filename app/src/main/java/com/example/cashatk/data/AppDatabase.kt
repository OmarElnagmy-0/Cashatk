package com.example.cashatk.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cashatk.utils.CategoryIconHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Category::class, Transaction::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    if (INSTANCE == null) {
                        val appContext = context.applicationContext
                        INSTANCE = Room.databaseBuilder(
                            appContext,
                            AppDatabase::class.java,
                            "cashatk_db"
                        )
                            .fallbackToDestructiveMigration(true)
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val categoryDao = getInstance(appContext).categoryDao()
                                        if (categoryDao.getAllCategories().isEmpty()) {
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
                            })
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}
