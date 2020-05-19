package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    fun getRecipe(id: Int) = repository.getRecipe(id)

    fun addRecipe(recipe: Recipe) = repository.insertRecipe(recipe)

    fun updateRecipe(recipe: Recipe) = repository.updateRecipe(recipe)
}
