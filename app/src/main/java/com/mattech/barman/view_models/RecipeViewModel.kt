package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    fun getRecipes(category: String) = repository.getRecipes(category)

    fun addRecipe(recipe: Recipe) = repository.insertRecipe(recipe)

    fun updateRecipe(recipe: Recipe) = repository.updateRecipe(recipe)

    fun deleteRecipe(recipe: Recipe) = repository.deleteRecipe(recipe)
}