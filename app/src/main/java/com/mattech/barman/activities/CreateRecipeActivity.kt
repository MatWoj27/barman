package com.mattech.barman.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.mattech.barman.R
import com.mattech.barman.adapters.IngredientAdapter
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.ingredients_edit_layout.*

const val IS_EDIT_TAG = "isEdit"

class CreateRecipeActivity : AppCompatActivity() {
    private val DISPLAY_INGREDIENT_LIST_TAG = "displayIngredientList"
    private var displayIngredientList = false
    private var isEdit: Boolean = true
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        setSupportActionBar(toolbar)
        title = getString(R.string.create_recipe_toolbar_title)
        isEdit = intent.getBooleanExtra(IS_EDIT_TAG, false)
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
        if (savedInstanceState != null && savedInstanceState.getBoolean(DISPLAY_INGREDIENT_LIST_TAG)) {
            showIngredientList()
        } else {
            add_ingredient_list_btn.setOnClickListener { showIngredientList() }
        }
        cancel_btn.setOnClickListener { onCancelClick() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DISPLAY_INGREDIENT_LIST_TAG, displayIngredientList)
    }

    override fun onBackPressed() {
        onCancelClick()
    }

    private fun showIngredientList() {
        displayIngredientList = true
        add_ingredient_list_btn.visibility = View.GONE
        ingredient_list_container.visibility = View.VISIBLE
        val ingredientAdapter = IngredientAdapter(arrayListOf(""), this) // TODO: if isEdit then take the ingredient list from the recipe
        ingredient_list.adapter = ingredientAdapter
        add_ingredient_btn.setOnClickListener { ingredientAdapter.add("") }
    }

    private fun onCancelClick() {
        if (isEdit) {
            finish()
//            TODO: display discard dialog if any changes were made to the original recipe
        } else {
            val name = recipe_name.text.trim()
            val description = recipe_description.text.trim()
            if (name.isEmpty() && description.isEmpty()) {
                finish()
            } else {
                AlertDialog.Builder(this)
                        .setMessage(R.string.cancel_message)
                        .setPositiveButton(R.string.yes) { _, _ -> finish() }
                        .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
                        .show()
            }
        }
    }
}
