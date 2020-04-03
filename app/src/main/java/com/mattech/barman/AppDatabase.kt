package com.mattech.barman

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mattech.barman.daos.RecipeDAO
import com.mattech.barman.models.Recipe
import com.mattech.barman.type_converters.RecipeTypeConverters

const val DATABASE_NAME = "appDatabase"

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(RecipeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getRecipeDAO(): RecipeDAO

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .build()
                    }
                }
            }
            return instance
        }
    }
}