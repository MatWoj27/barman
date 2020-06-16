package com.mattech.barman.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mattech.barman.models.Recipe
import com.mattech.barman.adapters.RecipeAdapter
import com.mattech.barman.R
import com.mattech.barman.utils.CircleTransformation
import com.mattech.barman.view_models.RecipeViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : AppCompatActivity() {
    private val RECIPE_CATEGORY_KEY = "recipeCategory"

    private lateinit var viewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter
    private var recipeCategory = Recipe.Category.LONG_DRINK.categoryName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        savedInstanceState?.let { recipeCategory = it.getString(RECIPE_CATEGORY_KEY)!! }
        viewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        setSupportActionBar(toolbar)
        presetRecipeList()
        presetNavigationHeader()
        presetNavigationMenu()
        presetDrawerToggle()
        add_drink_fab.setOnClickListener(::onAddClick)
    }

    override fun onResume() {
        super.onResume()
        add_drink_fab.isEnabled = true
        recipeAdapter.clickEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(RECIPE_CATEGORY_KEY, recipeCategory)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_list_menu, menu)
        val deleteItem = menu?.findItem(R.id.action_delete)
        deleteItem?.isVisible = viewModel.showDeleteAction.value == true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_delete -> {
            viewModel.deleteSelectedRecipes()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun presetRecipeList() {
        main_list.layoutManager = LinearLayoutManager(this)
        main_list.setHasFixedSize(true)
        main_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                outRect.top = if (position == 0) 16 else 0
                outRect.bottom = 16
            }
        })
        recipeAdapter = RecipeAdapter(context = this, selectedRecipes = viewModel.selectedRecipes)
        main_list.adapter = recipeAdapter
        viewModel.getRecipes(recipeCategory).observe(this, Observer { recipeAdapter.setRecipes(it) })
        viewModel.showDeleteAction.observe(this, Observer { invalidateOptionsMenu() })
    }

    private fun presetNavigationHeader() {
        val header = drawer.getHeaderView(0)
        val userPhoto = header.user_photo
        Picasso.with(this).load("https://api.adorable.io/avatars/128").transform(CircleTransformation()).into(userPhoto)
    }

    private fun presetNavigationMenu() {
        drawer.setNavigationItemSelectedListener { menuItem ->
            val result = when (menuItem.itemId) {
                R.id.long_drinks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.long_drinks), Toast.LENGTH_SHORT).show()
                    recipeCategory = Recipe.Category.LONG_DRINK.categoryName
                    true
                }
                R.id.short_drinks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.short_drinks), Toast.LENGTH_SHORT).show()
                    recipeCategory = Recipe.Category.SHORT_DRINK.categoryName
                    true
                }
                R.id.shots -> {
                    Toast.makeText(this@MainActivity, getString(R.string.shots), Toast.LENGTH_SHORT).show()
                    recipeCategory = Recipe.Category.SHOT.categoryName
                    true
                }
                R.id.snacks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.snacks), Toast.LENGTH_SHORT).show()
                    recipeCategory = Recipe.Category.SNACK.categoryName
                    true
                }
                else -> false
            }
            if (result) {
                viewModel.getRecipes(recipeCategory).observe(this, Observer { recipeAdapter.setRecipes(it) })
            }
            drawer_layout.closeDrawer(drawer)
            result
        }
    }

    private fun presetDrawerToggle() {
        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.open_drawer_description, R.string.close_drawer_description)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun onAddClick(view: View) {
        view.isEnabled = false
        val intent = Intent(applicationContext, CreateRecipeActivity::class.java)
                .apply {
                    putExtra(IS_EDIT_TAG, false)
                    putExtra(RECIPE_CATEGORY_TAG, recipeCategory)
                }
        startActivity(intent)
    }
}
