package com.mattech.barman.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mattech.barman.R
import kotlinx.android.synthetic.main.ingredient_item.view.*

class IngredientAdapter(ingredients: List<String>, context: Context) : ArrayAdapter<String>(context, R.layout.ingredient_item, ingredients) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val ingredient = getItem(position)
        val itemView = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false)
        itemView.ingredient.setText(ingredient)
        itemView.ingredient.setOnKeyListener { _, keyCode, _ -> handleKeyClick(keyCode, position) }
        return itemView
    }

    private fun handleKeyClick(keyCode: Int, position: Int) = when (keyCode) {
        KeyEvent.KEYCODE_ENTER -> enterClicked(position)
        else -> false
    }

    private fun enterClicked(position: Int): Boolean {
        insert("", position + 1)
        return true
    }
}