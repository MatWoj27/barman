package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mattech.barman.AppRepository

class ShowRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    fun getRecipe(id: Int) = repository.getRecipe(id)
}