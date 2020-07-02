package com.mattech.barman.view_models

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.AppRepository
import com.mattech.barman.R
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.utils.Resolution
import java.io.File
import java.io.IOException
import kotlin.collections.ArrayList

abstract class TextChangedWatcher : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
}

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    private var originalRecipe: Recipe? = null
    private var removePhoto = true
    private var tmpPhotoPath = ""

    var isEdit = false
    var recipeId: Int = 0
    var recipeName = ""
    var recipeDescription = ""
    var recipeCategory = Recipe.Category.LONG_DRINK.categoryName
    var photoPath = ""
    var ingredients: ArrayList<String> = arrayListOf()
    var focusedItemPosition = 0
    var displayIngredientList = false
        set(value) {
            field = value
            if (value && ingredients.size == 0) {
                ingredients.add("")
            }
        }

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
            deletePhotoFile(photoPath)
        }
    }

    private fun loadRecipe(recipe: Recipe) = with(recipe) {
        recipeName = name
        recipeDescription = description
        recipeCategory = category
        this@CreateRecipeViewModel.photoPath = photoPath
        if (ingredients.size > 0) {
            this@CreateRecipeViewModel.ingredients.addAll(ingredients)
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

    fun getPhotoIntent(context: Context): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                val photo = ImageUtil.createImageFile(context)
                tmpPhotoPath = photo.absolutePath
                val photoUri = FileProvider.getUriForFile(context, "com.mattech.barman.fileprovider", photo)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                return intent
            } catch (ex: IOException) {
                Log.e(javaClass.simpleName, "Failed to create a file for a photo: $ex")
                Toast.makeText(context, context.getString(R.string.camera_error_toast), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_camera_app_toast), Toast.LENGTH_SHORT).show()
        }
        return null
    }

    fun handlePhotoRequestResult(photoAccepted: Boolean) = if (photoAccepted) {
        ImageUtil.handleSamplingAndRotation(photoPath, Resolution.HIGH)
        photoPath = tmpPhotoPath
    } else {
        deletePhotoFile(tmpPhotoPath)
    }

    fun anyChangesApplied() = ((isEdit && originalRecipe != createRecipeFromUserInput()) || (!isEdit && !isEmptyDraft()))

    private fun deletePhotoFile(path: String) {
        val photoFile = File(path)
        photoFile.delete()
    }
}
