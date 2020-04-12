package com.mattech.barman.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mattech.barman.R
import com.mattech.barman.adapters.IngredientAdapter
import com.mattech.barman.adapters.IngredientListListener
import com.mattech.barman.utils.CircleTransformation
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.ingredients_edit_layout.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val IS_EDIT_TAG = "isEdit"

class CreateRecipeActivity : AppCompatActivity(), IngredientListListener {
    private val DISPLAY_INGREDIENT_LIST_KEY = "displayIngredientList"
    private val PHOTO_PATH_KEY = "photoPath"
    private val REQUEST_TAKE_PHOTO = 1
    private var displayIngredientList = false
    private var isEdit: Boolean = true
    private var recipeId: Int = -1
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        setSupportActionBar(toolbar)
        title = getString(R.string.create_recipe_toolbar_title)
        isEdit = intent.getBooleanExtra(IS_EDIT_TAG, false)
        recipeId = intent.getIntExtra(RECIPE_ID_TAG, -1)
        add_ingredient_list_btn.setOnClickListener { showIngredientList() }
        if (savedInstanceState != null && savedInstanceState.getBoolean(DISPLAY_INGREDIENT_LIST_KEY)) {
            showIngredientList()
        }
        photoPath = savedInstanceState?.getString(PHOTO_PATH_KEY)
        cancel_btn.setOnClickListener { onCancelClick() }
        add_photo.setOnClickListener { takePhoto() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(DISPLAY_INGREDIENT_LIST_KEY, displayIngredientList)
        outState.putString(PHOTO_PATH_KEY, photoPath)
    }

    override fun onBackPressed() {
        onCancelClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                add_photo.setImageBitmap(CircleTransformation().transform(bitmap))
            }
        }
    }

    override fun lastItemRemoved() {
        hideIngredientList()
    }

    private fun hideIngredientList() {
        displayIngredientList = false
        ingredient_list_container.visibility = View.GONE
        add_ingredient_list_btn.visibility = View.VISIBLE
    }

    private fun showIngredientList() {
        displayIngredientList = true
        add_ingredient_list_btn.visibility = View.GONE
        ingredient_list_container.visibility = View.VISIBLE
        val ingredients = arrayListOf("") // TODO: if isEdit then take the ingredient list from the recipe
        val ingredientAdapter = IngredientAdapter(ingredients, this, this)
        ingredient_list.adapter = ingredientAdapter
        ingredient_list.layoutManager = LinearLayoutManager(this)
        ingredient_list.isNestedScrollingEnabled = false
        add_ingredient_btn.setOnClickListener {
            ingredients.add("")
            ingredientAdapter.notifyItemInserted(ingredients.size - 1)
        }
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
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }
}
