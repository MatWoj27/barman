package com.mattech.barman.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(@PrimaryKey val id: Int,
                  val category: String,
                  val name: String,
                  val description: String,
                  val ingredients: ArrayList<String>)