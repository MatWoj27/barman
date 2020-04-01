package com.mattech.barman.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.mattech.barman.R
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
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
}
