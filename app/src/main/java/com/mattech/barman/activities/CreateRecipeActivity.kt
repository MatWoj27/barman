package com.mattech.barman.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.mattech.barman.R
import kotlinx.android.synthetic.main.activity_create_recipe.*

const val IS_EDIT_TAG = "isEdit"

class CreateRecipeActivity : AppCompatActivity() {
    private var isEdit: Boolean = true
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        setSupportActionBar(toolbar)
        title = getString(R.string.create_recipe_toolbar_title)
        isEdit = intent.getBooleanExtra(IS_EDIT_TAG, false)
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
        cancel_btn.setOnClickListener({ onCancelClick() })
    }

    override fun onBackPressed() {
        onCancelClick()
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
                        .setPositiveButton(R.string.yes, { _, _ -> finish() })
                        .setNegativeButton(R.string.no, { dialog, _ -> dialog.cancel() })
                        .show()
            }
        }
    }
}
