package com.mattech.barman.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.mattech.barman.models.Recipe
import com.mattech.barman.adapters.RecipeAdapter
import com.mattech.barman.R
import com.mattech.barman.utils.CircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : AppCompatActivity() {
    private val drinksAdapter = RecipeAdapter(getDrinks(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        main_list.layoutManager = LinearLayoutManager(this)
        main_list.adapter = drinksAdapter
        main_list.setHasFixedSize(true)
        main_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                outRect.top = if (position == 0) 16 else 0
                outRect.bottom = 16
            }
        })
        presetNavigationHeader()
        presetNavigationMenu()
        presetDrawerToggle()
        add_drink_fab.setOnClickListener { view ->
            view.isEnabled = false
            val intent = Intent(applicationContext, CreateRecipeActivity::class.java)
                    .apply { putExtra(IS_EDIT_TAG, false) }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        add_drink_fab.isEnabled = true
        drinksAdapter.clickEnabled = true
    }

    private fun getDrinks(): List<Recipe> {
        val drinks: ArrayList<Recipe> = ArrayList()
        drinks.add(Recipe(1, "Drink", "Cosmopolitan", "mix all together", arrayListOf("Vodka 40ml", "Cointreau 20ml")))
        drinks.add(Recipe(2, "Drink", "Mojito", "mix all together", arrayListOf("Vodka 40ml", "Mint", "Sugar 2 spoons")))
        drinks.add(Recipe(3, "Drink", "Sex on the beach", "mix all together", arrayListOf("Vodka 40ml", "Orange juice 100ml")))
        return drinks
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
//                   TO DO
                    true
                }
                R.id.short_drinks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.short_drinks), Toast.LENGTH_SHORT).show()
//                   TO DO
                    true
                }
                R.id.shots -> {
                    Toast.makeText(this@MainActivity, getString(R.string.shots), Toast.LENGTH_SHORT).show()
//                    TO DO
                    true
                }
                R.id.snacks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.snacks), Toast.LENGTH_SHORT).show()
//                    TO DO
                    true
                }
                else -> false
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
}
