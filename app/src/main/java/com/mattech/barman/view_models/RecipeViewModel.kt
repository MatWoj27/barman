package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private var category: String = ""
    val selectedRecipes: MutableSet<Int> = mutableSetOf()
    var showDeleteAction = false

    fun getRecipes(category: String): LiveData<List<Recipe>> {
        if (this.category != category) {
            selectedRecipes.clear()
            showDeleteAction = false
            this.category = category
        }
        return repository.getRecipes(category)
    }

    fun getRecipe(id: Int) = repository.getRecipe(id)

    fun deleteRecipe(recipe: Recipe) = repository.deleteRecipe(recipe)
}