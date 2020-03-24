package com.mattech.barman.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mattech.barman.R
import com.mattech.barman.activities.RECIPE_ID_TAG
import com.mattech.barman.activities.ShowRecipeActivity
import com.mattech.barman.models.Drink
import kotlinx.android.synthetic.main.drink_item.view.*

class DrinksAdapter(val drinks: List<Drink>, val context: Context) : RecyclerView.Adapter<DrinksAdapter.DrinkViewHolder>() {
    var clickEnabled = true

    inner class DrinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkName = view.drink_name
        val drinkPhoto = view.drink_photo

        init {
            view.setOnClickListener { itemClicked(adapterPosition) }
        }
    }

    override fun getItemCount(): Int {
        return drinks.size
    }

    override fun onCreateViewHolder(parentViewGroup: ViewGroup, viewType: Int): DrinkViewHolder {
        return DrinkViewHolder(LayoutInflater.from(context).inflate(R.layout.drink_item, parentViewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: DrinkViewHolder, position: Int) {
        viewHolder.drinkName.text = drinks[position].name
    }

    @Synchronized
    private fun itemClicked(position: Int) {
        if (clickEnabled) {
            val intent = Intent(context, ShowRecipeActivity::class.java)
                    .apply { putExtra(RECIPE_ID_TAG, drinks[position].id) }
            context.startActivity(intent)
            clickEnabled = false
        }
    }
}