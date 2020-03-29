package com.mattech.barman.daos

import androidx.room.*
import com.mattech.barman.models.Recipe

@Dao
interface RecipeDAO {

    @Query("SELECT * FROM recipes WHERE category LIKE :category")
    fun getRecipes(category: String): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipe(id: Int): Recipe

    @Insert
    fun insertRecipe(recipe: Recipe)

    @Update
    fun updateRecipe(recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)
}