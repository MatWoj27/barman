package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mattech.barman.AppRepository

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    fun getRecipes(category: String) = repository.getRecipes(category)
}