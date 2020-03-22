package com.mattech.barman.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mattech.barman.R
import com.mattech.barman.models.Drink
import kotlinx.android.synthetic.main.drink_item.view.*

class DrinksAdapter(val drinks: List<Drink>, val context: Context) : RecyclerView.Adapter<DrinksAdapter.DrinkViewHolder>() {

    class DrinkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drinkName = view.drink_name
        val drinkPhoto = view.drink_photo
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
}