package com.mattech.barman

import android.app.Application
import com.mattech.barman.daos.RecipeDAO
import com.mattech.barman.models.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

class AppRepository : CoroutineScope {
    private var recipeDAO: RecipeDAO
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    constructor(application: Application) {
        recipeDAO = AppDatabase.getInstance(application)!!.getRecipeDAO()
    }

    fun getRecipes(category: String) = recipeDAO.getRecipes(category)

    fun getRecipe(id: Int) = recipeDAO.getRecipe(id)

    fun insertRecipe(recipe: Recipe) = launch { insertRecipeBG(recipe) }

    private suspend fun insertRecipeBG(recipe: Recipe) = withContext(Dispatchers.IO) {
        recipeDAO.insertRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) = launch { updateRecipeBG(recipe) }

    private suspend fun updateRecipeBG(recipe: Recipe) = withContext(Dispatchers.IO) {
        recipeDAO.updateRecipe(recipe)
    }

    fun deleteRecipesById(recipes: Set<Recipe>) = launch { deleteRecipesByIdBG(recipes) }

    private suspend fun deleteRecipesByIdBG(recipes: Set<Recipe>) = withContext(Dispatchers.IO) {
        recipes.filter { it.photoPath.isNotEmpty() }.forEach { File(it.photoPath).delete() }
        recipeDAO.deleteRecipesById(recipes.map { it.id }.toSet())
    }
}