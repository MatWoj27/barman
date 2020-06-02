package com.mattech.barman.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mattech.barman.R
import com.mattech.barman.activities.RECIPE_ID_TAG
import com.mattech.barman.activities.ShowRecipeActivity
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.utils.ViewAnimator
import com.mattech.barman.utils.toBitmap
import kotlinx.android.synthetic.main.recipe_item.view.*

class RecipeAdapter(private val recipes: MutableList<Recipe> = mutableListOf(), val context: Context, val selectedRecipes: MutableSet<Int>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    var clickEnabled = true

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.recipe_name
        val recipePhoto: ImageView = view.recipe_photo

        init {
            view.setOnClickListener { itemClicked(adapterPosition) }
            view.setOnLongClickListener {
                val id = recipes[adapterPosition].id
                val isSelected = selectedRecipes.contains(id)
                isSelected.let {
                    if (it) {
                        selectedRecipes.remove(id)
                    } else {
                        selectedRecipes.add(id)
                    }
                    animateCheckedPhotoChange(!it)
                }
                true
            }
        }

        private fun animateCheckedPhotoChange(select: Boolean) {
            val photoBitmap = if (select) {
                context.getDrawable(R.drawable.checked_item)?.toBitmap()
            } else {
                ImageUtil.getBitmap(recipes[adapterPosition].photoPath)
                        ?: context.getDrawable(R.drawable.photo_placeholder)?.toBitmap()
            }
            photoBitmap?.let {
                ViewAnimator.animatedImageChange(context, recipePhoto, it)
            }
        }
    }

    override fun getItemCount() = recipes.size

    override fun onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_item, parentViewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: RecipeViewHolder, position: Int) = recipes[position].let {
        viewHolder.recipeName.text = it.name
        if (selectedRecipes.contains(it.id)) {
            viewHolder.recipePhoto.setImageDrawable(context.getDrawable(R.drawable.checked_item))
        } else {
            displayPhotoIfExists(viewHolder.recipePhoto, it.photoPath)
        }
    }

    @Synchronized
    private fun itemClicked(position: Int) {
        if (clickEnabled) {
            val intent = Intent(context, ShowRecipeActivity::class.java)
                    .apply { putExtra(RECIPE_ID_TAG, recipes[position].id) }
            context.startActivity(intent)
            clickEnabled = false
        }
    }

    private fun displayPhotoIfExists(imageView: ImageView, photoPath: String) {
        ImageUtil.getBitmap(photoPath)?.let { imageView.setImageBitmap(it) }
                ?: imageView.setImageDrawable(context.getDrawable(R.drawable.photo_placeholder))
    }

    fun setRecipes(recipes: List<Recipe>) {
        if (recipes.size < this.recipes.size) {
            val removedRecipes = this.recipes.filter { recipe -> !recipes.contains(recipe) }
            removedRecipes.map { recipe -> this.recipes.indexOf(recipe) }.asReversed().forEach { notifyItemRemoved(it) }
            this.recipes.removeAll(removedRecipes)
        } else if (this.recipes.size > 0 && recipes.size > this.recipes.size) {
            val addedRecipe = recipes.first { recipe -> !this.recipes.contains(recipe) }
            this.recipes.add(addedRecipe)
            notifyItemInserted(recipes.size - 1)
        } else {
            this.recipes.clear()
            this.recipes.addAll(recipes)
            notifyDataSetChanged()
        }
    }
}