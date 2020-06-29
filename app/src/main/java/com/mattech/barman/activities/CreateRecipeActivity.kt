package com.mattech.barman.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.R
import com.mattech.barman.adapters.IngredientAdapter
import com.mattech.barman.adapters.IngredientListListener
import com.mattech.barman.dialogs.ConfirmationDialogFragment
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.CircleTransformation
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.view_models.CreateRecipeViewModel
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.ingredients_edit_layout.*

const val IS_EDIT_TAG = "isEdit"
const val RECIPE_CATEGORY_TAG = "recipeCategory"

class CreateRecipeActivity : AppCompatActivity(), IngredientListListener, ConfirmationDialogFragment.ConfirmActionListener {
    private val REQUEST_TAKE_PHOTO = 1

    private lateinit var viewModel: CreateRecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        viewModel = ViewModelProvider(this).get(CreateRecipeViewModel::class.java)
        viewModel.isEdit = intent.getBooleanExtra(IS_EDIT_TAG, false)
        presetToolbar()
        if (viewModel.isEdit) {
            viewModel.recipeId = intent.getIntExtra(RECIPE_ID_TAG, 0)
            viewModel.getRecipe().observe(this, Observer { displayRecipe(it) })
        } else {
            viewModel.recipeCategory = intent.getStringExtra(RECIPE_CATEGORY_TAG)
            displayPhotoThumbnailAsAddPhoto()
            if (viewModel.displayIngredientList) {
                showIngredientList()
            }
        }
        recipe_name.addTextChangedListener(viewModel.recipeNameWatcher)
        recipe_description.addTextChangedListener(viewModel.recipeDescriptionWatcher)
        add_ingredient_list_btn.setOnClickListener { showIngredientList() }
        save_btn.setOnClickListener { onSaveClick() }
        cancel_btn.setOnClickListener { onCancelClick() }
        add_photo.setOnClickListener { takePhoto() }
    }

    override fun onBackPressed() = onCancelClick()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            val photoAccepted = resultCode == RESULT_OK
            viewModel.handlePhotoRequestResult(photoAccepted)
            if (photoAccepted) {
                displayPhotoThumbnailAsAddPhoto()
            }
        }
    }

    override fun lastItemRemoved() = hideIngredientList()

    override fun focusedItemChanged(position: Int) {
        viewModel.focusedItemPosition = position
    }

    override fun onConfirm() = finish()

    override fun onReject() {}

    private fun presetToolbar() {
        setSupportActionBar(toolbar)
        title = if (viewModel.isEdit) getString(R.string.edit_recipe_toolbar_title) else getString(R.string.create_recipe_toolbar_title)
    }

    private fun hideIngredientList() {
        viewModel.displayIngredientList = false
        ingredient_list_container.visibility = View.GONE
        add_ingredient_list_btn.visibility = View.VISIBLE
    }

    private fun showIngredientList() {
        viewModel.displayIngredientList = true
        add_ingredient_list_btn.visibility = View.GONE
        ingredient_list_container.visibility = View.VISIBLE
        val ingredientAdapter = IngredientAdapter(viewModel.ingredients, this, this, viewModel.focusedItemPosition)
        ingredient_list.apply {
            adapter = ingredientAdapter
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            isNestedScrollingEnabled = false
        }
        add_ingredient_btn.setOnClickListener {
            viewModel.ingredients.add("")
            viewModel.focusedItemPosition = viewModel.ingredients.size - 1
            ingredientAdapter.focusedItemPosition = viewModel.focusedItemPosition
            ingredientAdapter.notifyItemInserted(viewModel.focusedItemPosition)
        }
        recipe_name.onFocusChangeListener = FocusMovedListener(ingredientAdapter)
        recipe_description.onFocusChangeListener = FocusMovedListener(ingredientAdapter)
    }

    private fun displayRecipe(recipe: Recipe) {
        recipe_name.setText(recipe.name)
        if (viewModel.displayIngredientList) {
            showIngredientList()
        }
        recipe_description.setText(recipe.description)
        displayPhotoThumbnailAsAddPhoto()
    }

    private fun displayPhotoThumbnailAsAddPhoto() = ImageUtil.getBitmap(viewModel.photoPath)?.let {
        ImageViewCompat.setImageTintList(add_photo, null)
        add_photo.setImageBitmap(CircleTransformation().transform(it))
    }

    private fun onSaveClick() = if (recipe_name.text.isNotBlank()) {
        viewModel.saveRecipe()
        finish()
    } else {
        Toast.makeText(this, R.string.define_name_message, Toast.LENGTH_SHORT).show()
    }

    private fun onCancelClick() {
        if (viewModel.anyChangesApplied()) {
            ConfirmationDialogFragment.newInstance(getString(R.string.cancel_message)).apply {
                show(supportFragmentManager, null)
            }
        } else {
            finish()
        }
    }

    private fun takePhoto() = viewModel.getPhotoIntent(this)?.let { startActivityForResult(it, REQUEST_TAKE_PHOTO) }

    inner class FocusMovedListener(private val adapter: IngredientAdapter) : View.OnFocusChangeListener {
        override fun onFocusChange(p0: View?, hasFocus: Boolean) {
            if (hasFocus) {
                viewModel.focusedItemPosition = RecyclerView.NO_POSITION
                adapter.focusedItemPosition = RecyclerView.NO_POSITION
            }
        }
    }
}
