package com.mattech.barman.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe
import java.io.File

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    var isEdit = false
    var recipeId: Int = 0
    var recipeCategory: String = Recipe.Category.LONG_DRINK.categoryName
    var photoPath = ""
    var ingredients = arrayListOf("")
    var focusedItemPosition = 0
    var displayIngredientList = false
    var removePhoto = true

    fun getRecipe() = repository.getRecipe(recipeId)

    fun addRecipe(recipe: Recipe) {
        removePhoto = false
        repository.insertRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) = repository.updateRecipe(recipe)

    override fun onCleared() {
        super.onCleared()
        if (!isEdit && photoPath.isNotEmpty() && removePhoto) {
            deletePhotoFile()
        }
    }

    fun getNonBlankIngredientList() = ingredients.filterNot { it.isBlank() } as ArrayList<String>

    fun deletePhotoFile() {
        val photoFile = File(photoPath)
        photoFile.delete()
    }
}
