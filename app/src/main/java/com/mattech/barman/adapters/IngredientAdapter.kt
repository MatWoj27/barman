package com.mattech.barman.adapters

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.R
import kotlinx.android.synthetic.main.ingredient_item.view.*

interface IngredientListListener {
    fun lastItemRemoved()
}

class IngredientAdapter(val ingredients: ArrayList<String>, val context: Context, val listener: IngredientListListener) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredient = itemView.ingredient

        init {
            ingredient.setOnKeyListener { view, keyCode, keyEvent -> handleKeyClick(keyEvent, keyCode, position, view.ingredient) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        return IngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false))
    }

    override fun onBindViewHolder(viewHolder: IngredientViewHolder, position: Int) {
        viewHolder.ingredient.setText(ingredients[position])
    }

    override fun getItemCount() = ingredients.size

    private fun handleKeyClick(keyEvent: KeyEvent, keyCode: Int, position: Int, view: EditText) = if (keyEvent.action == KeyEvent.ACTION_DOWN) {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> enterClicked(position)
            KeyEvent.KEYCODE_DEL -> backspaceClicked(position, view)
            else -> false
        }
    } else {
        false
    }

    private fun enterClicked(position: Int): Boolean {
        ingredients.add(position + 1, "")
        notifyItemInserted(position + 1)
        return true
    }

    private fun backspaceClicked(position: Int, view: EditText) = if (view.text.isEmpty() && position < itemCount) {
        ingredients.removeAt(position)
        notifyItemRemoved(position)
        if (itemCount == 0) {
            listener.lastItemRemoved()
        }
        true
    } else {
        false
    }
}