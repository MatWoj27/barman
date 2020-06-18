package com.mattech.barman.view_models

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.AppRepository
import com.mattech.barman.models.Recipe
import java.io.File
import kotlin.collections.ArrayList

abstract class TextChangedWatcher : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    private var originalRecipe: Recipe? = null
    private var removePhoto = true

    var isEdit = false
    var recipeId: Int = 0
    var recipeName = ""
    var recipeDescription = ""
    var recipeCategory: String = Recipe.Category.LONG_DRINK.categoryName
    var photoPath = ""
    var ingredients = arrayListOf("")
    var focusedItemPosition = 0
    var displayIngredientList = false

    val recipeNameWatcher = object : TextChangedWatcher() {
        override fun afterTextChanged(text: Editable?) {
            recipeName = text.toString()
        }
    }

    val recipeDescriptionWatcher = object : TextChangedWatcher() {
        override fun afterTextChanged(text: Editable?) {
            recipeDescription = text.toString()
        }
    }

    fun getRecipe() = Transformations.map(repository.getRecipe(recipeId)) { recipe ->
        if (originalRecipe == null) {
            originalRecipe = recipe
            loadRecipe(recipe)
            recipe
        } else {
            createRecipeFromUserInput()
        }
    }

    fun saveRecipe() {
        val recipe = createRecipeFromUserInput()
        if (isEdit) {
            originalRecipe?.let { if (it != recipe) updateRecipe(recipe) }
        } else {
            addRecipe(recipe)
        }
    }

    private fun addRecipe(recipe: Recipe) {
        removePhoto = false
        repository.insertRecipe(recipe)
    }

    private fun updateRecipe(recipe: Recipe) = repository.updateRecipe(recipe)

    override fun onCleared() {
        super.onCleared()
        if (!isEdit && photoPath.isNotEmpty() && removePhoto) {
            deletePhotoFile()
        }
    }

    private fun loadRecipe(recipe: Recipe) {
        recipeName = recipe.name
        recipeDescription = recipe.description
        recipeCategory = recipe.category
        photoPath = recipe.photoPath
        if (recipe.ingredients.size > 0) {
            ingredients.clear()
            ingredients.addAll(recipe.ingredients)
            focusedItemPosition = RecyclerView.NO_POSITION
            displayIngredientList = true
        }
    }

    private fun createRecipeFromUserInput() = Recipe(recipeId,
            recipeCategory,
            recipeName,
            recipeDescription,
            photoPath,
            getNonBlankIngredientList())

    private fun isEmptyDraft(): Boolean {
        val name = recipeName.trim()
        val description = recipeDescription.trim()
        val ingredientCount = getNonBlankIngredientList().size
        return name.isEmpty() && description.isEmpty() && ingredientCount == 0 && photoPath.isEmpty()
    }

    private fun getNonBlankIngredientList() = ingredients.filterNot { it.isBlank() } as ArrayList<String>

    fun anyChangesApplied() = ((isEdit && originalRecipe != createRecipeFromUserInput()) || (!isEdit && !isEmptyDraft()))

    fun deletePhotoFile() {
        val photoFile = File(photoPath)
        photoFile.delete()
        photoPath = ""
    }
}
