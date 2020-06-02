package com.mattech.barman.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.R
import com.mattech.barman.adapters.IngredientAdapter
import com.mattech.barman.adapters.IngredientListListener
import com.mattech.barman.dialogs.ConfirmationDialogFragment
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.CircleTransformation
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.utils.Resolution
import com.mattech.barman.view_models.CreateRecipeViewModel
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.ingredients_edit_layout.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val IS_EDIT_TAG = "isEdit"
const val RECIPE_CATEGORY_TAG = "recipeCategory"

class CreateRecipeActivity : AppCompatActivity(), IngredientListListener, ConfirmationDialogFragment.ConfirmActionListener {
    private val DISPLAY_INGREDIENT_LIST_KEY = "displayIngredientList"
    private val PHOTO_PATH_KEY = "photoPath"
    private val INGREDIENTS_KEY = "ingredients"
    private val FOCUSED_ITEM_POSITION_KEY = "focusedItemPosition"
    private val REQUEST_TAKE_PHOTO = 1

    private lateinit var viewModel: CreateRecipeViewModel

    private var displayIngredientList = false
    private var isEdit = false
    private var recipeId: Int = 0
    private var recipeCategory: String = Recipe.Category.LONG_DRINK.categoryName
    private var photoPath = ""
    private var ingredients = arrayListOf("")
    private var focusedItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        setSupportActionBar(toolbar)
        title = getString(R.string.create_recipe_toolbar_title)
        viewModel = ViewModelProviders.of(this).get(CreateRecipeViewModel::class.java)
        isEdit = intent.getBooleanExtra(IS_EDIT_TAG, false)
        if (savedInstanceState != null) {
            savedInstanceState.getString(PHOTO_PATH_KEY)?.let {
                if (it.isNotEmpty()) {
                    photoPath = it
                    displayPhotoThumbnailAsAddPhoto()
                }
            }
            if (savedInstanceState.getBoolean(DISPLAY_INGREDIENT_LIST_KEY)) {
                savedInstanceState.getStringArrayList(INGREDIENTS_KEY)?.let {
                    ingredients = it
                    focusedItemPosition = savedInstanceState.getInt(FOCUSED_ITEM_POSITION_KEY)
                }
                showIngredientList()
            }
        } else if (isEdit) {
            recipeId = intent.getIntExtra(RECIPE_ID_TAG, 0)
            viewModel.getRecipe(recipeId).observe(this, Observer<Recipe> {
                recipeCategory = it.category
                photoPath = it.photoPath
                if (it.ingredients.size > 0) {
                    focusedItemPosition = RecyclerView.NO_POSITION
                }
                displayRecipe(it)
            })
        } else {
            recipeCategory = intent.getStringExtra(RECIPE_CATEGORY_TAG)
        }
        add_ingredient_list_btn.setOnClickListener { showIngredientList() }
        save_btn.setOnClickListener { onSaveClick() }
        cancel_btn.setOnClickListener { onCancelClick() }
        add_photo.setOnClickListener { takePhoto() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DISPLAY_INGREDIENT_LIST_KEY, displayIngredientList)
        outState.putString(PHOTO_PATH_KEY, photoPath)
        outState.putStringArrayList(INGREDIENTS_KEY, ingredients)
        outState.putInt(FOCUSED_ITEM_POSITION_KEY, focusedItemPosition)
    }

    override fun onBackPressed() {
        onCancelClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                ImageUtil.handleSamplingAndRotation(photoPath, Resolution.HIGH)
                displayPhotoThumbnailAsAddPhoto()
            } else {
                deletePhotoFile()
                photoPath = ""
            }
        }
    }

    override fun lastItemRemoved() {
        hideIngredientList()
    }

    override fun focusedItemChanged(position: Int) {
        focusedItemPosition = position
    }

    override fun onConfirm() {
        if (photoPath.isNotEmpty()) {
            deletePhotoFile()
        }
        finish()
    }

    override fun onReject() {}

    private fun hideIngredientList() {
        displayIngredientList = false
        ingredient_list_container.visibility = View.GONE
        add_ingredient_list_btn.visibility = View.VISIBLE
    }

    private fun showIngredientList() {
        displayIngredientList = true
        add_ingredient_list_btn.visibility = View.GONE
        ingredient_list_container.visibility = View.VISIBLE
        if (ingredients.size == 0) {
            ingredients.add("")
        }
        val ingredientAdapter = IngredientAdapter(ingredients, this, this, focusedItemPosition)
        ingredient_list.adapter = ingredientAdapter
        ingredient_list.layoutManager = LinearLayoutManager(this)
        ingredient_list.isNestedScrollingEnabled = false
        add_ingredient_btn.setOnClickListener {
            ingredients.add("")
            focusedItemPosition = ingredients.size - 1
            ingredientAdapter.focusedItemPosition = focusedItemPosition
            ingredientAdapter.notifyItemInserted(focusedItemPosition)
        }
        recipe_name.onFocusChangeListener = FocusMovedListener(ingredientAdapter)
        recipe_description.onFocusChangeListener = FocusMovedListener(ingredientAdapter)
    }

    private fun displayRecipe(recipe: Recipe) {
        recipe_name.setText(recipe.name)
        if (recipe.ingredients.size > 0) {
            ingredients = recipe.ingredients
            showIngredientList()
        }
        recipe_description.setText(recipe.description)
        if (photoPath.isNotEmpty()) {
            displayPhotoThumbnailAsAddPhoto()
        }
    }

    private fun displayPhotoThumbnailAsAddPhoto() {
        ImageUtil.getBitmap(photoPath)?.let {
            ImageViewCompat.setImageTintList(add_photo, null)
            add_photo.setImageBitmap(CircleTransformation().transform(it))
        }
    }

    private fun onSaveClick() {
        if (recipe_name.text.isNotBlank()) {
            val recipe = createRecipeFromUserInput()
            if (isEdit) {
                // TODO: should be updated only if any change was made
                viewModel.updateRecipe(recipe)
            } else {
                viewModel.addRecipe(recipe)
            }
            finish()
        } else {
            Toast.makeText(this, R.string.define_name_message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onCancelClick() {
        if (isEdit) {
            finish()
//            TODO: display discard dialog if any changes were made to the original recipe
        } else {
            if (isEmptyDraft()) {
                finish()
            } else {
                ConfirmationDialogFragment.newInstance(getString(R.string.cancel_message)).apply {
                    show(supportFragmentManager, null)
                }
            }
        }
    }

    private fun isEmptyDraft(): Boolean {
        val name = recipe_name.text.trim()
        val description = recipe_description.text.trim()
        val ingredientCount = getNonBlankIngredientList().size
        return name.isEmpty() && description.isEmpty() && ingredientCount == 0 && photoPath.isEmpty()
    }

    private fun getNonBlankIngredientList() = ingredients.filterNot { it.isBlank() } as ArrayList<String>

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                val photo = createImageFile()
                photoPath = photo.absolutePath
                val photoUri = FileProvider.getUriForFile(this, "com.mattech.barman.fileprovider", photo)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            } catch (ex: IOException) {
                Log.e(javaClass.simpleName, "Failed to create a file for a photo: $ex")
                Toast.makeText(this, getString(R.string.camera_error_toast), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.no_camera_app_toast), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("WEBP_${timeStamp}_", ".webp", storageDir)
    }

    private fun createRecipeFromUserInput() = Recipe(recipeId,
            recipeCategory,
            recipe_name.text.toString(),
            recipe_description.text.toString(),
            photoPath,
            getNonBlankIngredientList())

    private fun deletePhotoFile() {
        val photoFile = File(photoPath)
        photoFile.delete()
    }

    inner class FocusMovedListener(private val adapter: IngredientAdapter) : View.OnFocusChangeListener {
        override fun onFocusChange(p0: View?, hasFocus: Boolean) {
            if (hasFocus) {
                focusedItemPosition = RecyclerView.NO_POSITION
                adapter.focusedItemPosition = focusedItemPosition
            }
        }
    }
}
