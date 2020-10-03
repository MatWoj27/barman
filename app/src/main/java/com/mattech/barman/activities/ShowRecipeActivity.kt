package com.mattech.barman.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.mattech.barman.R
import com.mattech.barman.adapters.RecipeContentPageAdapter
import com.mattech.barman.databinding.ActivityShowRecipeBinding
import com.mattech.barman.fragments.DescriptionActionListener
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.view_models.ShowRecipeViewModel
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_show_recipe.*

const val RECIPE_ID_TAG = "recipeId"

class ShowRecipeActivity : AppCompatActivity(), DescriptionActionListener {
    private var recipeId: Int = -1
    private var clickEnabled = true
    private lateinit var binding: ActivityShowRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_recipe)
        setSupportActionBar(toolbar)
        makeStatusBarTransparent()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
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
            onEditActionSelected()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @Synchronized
    override fun addDescription() {
        onEditActionSelected()
    }

    private fun onEditActionSelected() {
        if (clickEnabled) {
            val intent = Intent(this, CreateRecipeActivity::class.java).apply {
                putExtra(IS_EDIT_TAG, true)
                putExtra(RECIPE_ID_TAG, recipeId)
            }
            startActivity(intent)
            clickEnabled = false
        }
    }

    private fun makeStatusBarTransparent() = window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
    }

    private fun displayRecipe(recipe: Recipe) = with(recipe) {
        binding.recipe = recipe
        setUpRecipePager(recipe)
        displayPhotoIfExists(photoPath)
    }

    private fun setUpRecipePager(recipe: Recipe) {
        pager.adapter = RecipeContentPageAdapter(this, recipe)
        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = getString(if (position == 0) R.string.preparation else R.string.ingredients)
        }.attach()
    }

    private fun displayPhotoIfExists(photoPath: String) = ImageUtil.getBitmap(photoPath)?.let {
        recipe_photo.setImageBitmap(it)
        displayBlurryBackgroundIfLandscape(it)
    }

    private fun displayBlurryBackgroundIfLandscape(photo: Bitmap) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Blurry.with(this).from(photo).into(background)
        }
    }
}
