package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private var category: String = ""
    val selectedRecipes = MutableIdSet()
    var showDeleteAction = MutableLiveData<Boolean>(false)

    inner class MutableIdSet : HashSet<Int>() {
        override fun remove(element: Int): Boolean {
            val result = super.remove(element)
            if (result && size == 0) {
                showDeleteAction.value = false
            }
            return result
        }

        override fun add(element: Int): Boolean {
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

    fun getRecipes(category: String): LiveData<List<Recipe>> {
        if (this.category != category) {
            selectedRecipes.clear()
            this.category = category
        }
        return repository.getRecipes(category)
    }

    fun getRecipe(id: Int) = repository.getRecipe(id)

    fun deleteSelectedRecipes() {
        val idSet = HashSet(selectedRecipes)
        repository.deleteRecipesById(idSet)
        selectedRecipes.clear()
    }
}