package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    val selectedRecipes = MutableRecipeSet()
    var showDeleteAction = MutableLiveData<Boolean>(false)
    var category = Recipe.Category.LONG_DRINK.categoryName
        set(value) {
            if (field != value) {
                selectedRecipes.clear()
                field = value
                recipes?.let {
                    repository.getRecipes(value).observeForever { recipes?.postValue(it) }
                }
            }
        }

    private var recipes: MutableLiveData<List<Recipe>>? = null

    inner class MutableRecipeSet : HashSet<Recipe>() {
        override fun remove(element: Recipe): Boolean {
            val result = super.remove(element)
            if (result && size == 0) {
                showDeleteAction.value = false
            }
            return result
        }

        override fun add(element: Recipe): Boolean {
            val result = super.add(element)
            if (result && size == 1) {
                showDeleteAction.value = true
            }
            return result
        }

        override fun clear() {
            super.clear()
            showDeleteAction.value = false
        }
    }

    fun getRecipes() = Transformations.switchMap(repository.getRecipes(category)) {
        if (recipes == null) {
            recipes = MutableLiveData(it)
        }
        recipes
    }

    fun deleteSelectedRecipes() {
        val recipeSet = HashSet(selectedRecipes)
        repository.deleteRecipesById(recipeSet)
        selectedRecipes.clear()
    }
}