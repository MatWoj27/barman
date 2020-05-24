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

interface SelectionListener {
    fun itemSelected(position: Int)
}

class RecipeAdapter(private val recipes: MutableList<Recipe> = mutableListOf(), val context: Context, val selectedRecipes: MutableSet<Int>, val listener: SelectionListener) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    var clickEnabled = true

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.recipe_name
        val recipePhoto: ImageView = view.recipe_photo

        init {
            view.setOnClickListener { itemClicked(adapterPosition) }
            view.setOnLongClickListener {
                val isSelected = selectedRecipes.contains(adapterPosition)
                if (isSelected) {
                    selectedRecipes.remove(adapterPosition)
                } else {
                    selectedRecipes.add(adapterPosition)
                }
                listener.itemSelected(adapterPosition)
                animateCheckedPhotoChange(!isSelected)
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

    override fun onBindViewHolder(viewHolder: RecipeViewHolder, position: Int) {
        viewHolder.recipeName.text = recipes[position].name
        displayPhotoIfExists(viewHolder.recipePhoto, recipes[position].photoPath)
        if (selectedRecipes.contains(position)) {
            viewHolder.recipePhoto.setImageDrawable(context.getDrawable(R.drawable.checked_item))
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
        this.recipes.clear()
        this.recipes.addAll(recipes)
        notifyDataSetChanged()
    }
}