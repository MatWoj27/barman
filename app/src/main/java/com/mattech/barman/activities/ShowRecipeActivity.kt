package com.mattech.barman.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mattech.barman.R
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.view_models.ShowRecipeViewModel
import kotlinx.android.synthetic.main.activity_show_recipe.*

const val RECIPE_ID_TAG = "recipeId"

class ShowRecipeActivity : AppCompatActivity() {
    private var recipeId: Int = -1
    private var clickEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_recipe)
        setSupportActionBar(toolbar)
        makeStatusBarTransparent()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        recipe_description.movementMethod = ScrollingMovementMethod()
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
        val viewModel = ViewModelProvider(this).get(ShowRecipeViewModel::class.java)
        viewModel.getRecipe(recipeId).observe(this, Observer { displayRecipe(it) })
    }

    override fun onResume() {
        super.onResume()
        clickEnabled = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.show_recipe_menu, menu)
        return true
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

    private fun makeStatusBarTransparent() = window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
    }

    private fun displayRecipe(recipe: Recipe) = with(recipe) {
        recipe_name.text = name
        displayDescriptionIfDefined(description)
        displayIngredientsIfDefined(ingredients)
        displayPhotoIfExists(photoPath)
    }

    private fun displayPhotoIfExists(photoPath: String) =
            ImageUtil.getBitmap(photoPath)?.let { recipe_photo.setImageBitmap(it) }

    private fun displayIngredientsIfDefined(ingredients: List<String>) = if (ingredients.isNotEmpty()) {
        ingredient_list_container.visibility = View.VISIBLE
        ingredients_list.adapter = ArrayAdapter<String>(this, R.layout.ingredient_show_item, ingredients)
    } else {
        ingredient_list_container.visibility = View.GONE
    }

    private fun displayDescriptionIfDefined(description: String) = if (description.isNotBlank()) {
        recipe_description_container.visibility = View.VISIBLE
        recipe_description.text = description
    } else {
        recipe_description_container.visibility = View.GONE
    }
}
