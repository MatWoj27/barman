package com.mattech.barman.type_converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeTypeConverters {

    @TypeConverter
    fun arrayListToString(list: ArrayList<String>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToArrayList(data: String): ArrayList<String>? {
        if (data == null) {
            return null
        }
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(data, type)
    }
}