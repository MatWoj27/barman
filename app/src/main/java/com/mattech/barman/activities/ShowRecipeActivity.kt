package com.mattech.barman.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mattech.barman.R
import com.mattech.barman.models.Recipe
import com.mattech.barman.view_models.RecipeViewModel
import kotlinx.android.synthetic.main.activity_show_recipe.*
import java.io.File

const val RECIPE_ID_TAG = "recipeId"

class ShowRecipeActivity : AppCompatActivity() {
    private var recipeId: Int = -1
    private var clickEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)
        setSupportActionBar(toolbar)
        makeStatusBarTransparent()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
        val viewModel = ViewModelProviders.of(this).get(RecipeViewModel::class.java)
        viewModel.getRecipe(recipeId).observe(this, Observer<Recipe> { displayRecipe(it) })
    }

    override fun onResume() {
        super.onResume()
        clickEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_recipe_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_edit -> {
            synchronized(this) {
                if (clickEnabled) {
                    val intent = Intent(this, CreateRecipeActivity::class.java).apply {
                        putExtra(IS_EDIT_TAG, true)
                        putExtra(RECIPE_ID_TAG, recipeId)
                    }
                    startActivity(intent)
                    clickEnabled = false
                }
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun makeStatusBarTransparent() {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        recipe_name.text = recipe.name
        recipe_description.text = recipe.description
        displayIngredients(recipe.ingredients)
        displayPhotoIfExists(recipe.photoPath)
    }

    private fun displayPhotoIfExists(photoPath: String) {
        val imageFile = File(photoPath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            recipe_photo.setImageBitmap(bitmap)
        }
    }

    private fun displayIngredients(ingredients: List<String>) {
        ingredients_list.adapter = ArrayAdapter<String>(this, R.layout.ingredient_show_item, ingredients)
    }
}
