package com.mattech.barman.adapters

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.mattech.barman.R
import com.mattech.barman.activities.RECIPE_ID_TAG
import com.mattech.barman.activities.ShowRecipeActivity
import com.mattech.barman.databinding.RecipeItemBinding
import com.mattech.barman.models.Recipe
import com.mattech.barman.utils.ImageUtil
import com.mattech.barman.utils.ViewAnimator
import com.mattech.barman.utils.toBitmap
import kotlinx.android.synthetic.main.recipe_item.view.*

class RecipeAdapter(private val recipes: MutableList<Recipe> = mutableListOf(), val context: Context, val selectedRecipes: MutableSet<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private var category = ""
    var clickEnabled = true

    inner class RecipeViewHolder(private val binding: RecipeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val recipePhoto: ImageView = binding.root.recipe_photo

        init {
            val view = binding.root
            view.setOnClickListener { itemClicked(adapterPosition) }
            view.setOnLongClickListener {
                recipes[adapterPosition].let {
                    val isSelected = selectedRecipes.contains(it)
                    if (isSelected) {
                        selectedRecipes.remove(it)
                    } else {
                        selectedRecipes.add(it)
                    }
                    animateCheckedPhotoChange(!isSelected)
                }
                true
            }
        }

        fun bind(recipe: Recipe) {
            binding.recipe = recipe
            binding.executePendingBindings()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecipeViewHolder(RecipeItemBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(viewHolder: RecipeViewHolder, position: Int) = recipes[position].let {
        viewHolder.bind(it)
        if (selectedRecipes.contains(it)) {
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

    private fun displayPhotoIfExists(imageView: ImageView, photoPath: String) = ImageUtil.getBitmap(photoPath)?.let { imageView.setImageBitmap(it) }
            ?: imageView.setImageDrawable(context.getDrawable(R.drawable.photo_placeholder))

    fun setRecipes(recipes: List<Recipe>, category: String) {
        val isNewDataSet = category != this.category
        if (!isNewDataSet) {
            if (recipes.size < this.recipes.size) {
                val removedRecipes = this.recipes.filter { !recipes.contains(it) }
                removedRecipes.map { recipe -> this.recipes.indexOf(recipe) }.asReversed().forEach { notifyItemRemoved(it) }
                this.recipes.removeAll(removedRecipes)
            } else if (recipes.size > this.recipes.size) {
                val addedRecipe = recipes.first { !this.recipes.contains(it) }
                this.recipes.add(addedRecipe)
                notifyItemInserted(recipes.size - 1)
            } else {
                this.recipes.firstOrNull { !recipes.contains(it) }?.let {
                    val modifiedRecipeIndex = this.recipes.indexOf(it)
                    this.recipes[modifiedRecipeIndex] = recipes[modifiedRecipeIndex]
                    notifyItemChanged(modifiedRecipeIndex)
                }
            }
        } else {
            this.category = category
            this.recipes.clear()
            this.recipes.addAll(recipes)
            notifyDataSetChanged()
        }
    }
}