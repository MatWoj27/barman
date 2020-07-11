package com.mattech.barman.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.mattech.barman.R
import kotlinx.android.synthetic.main.ingredient_edit_item.view.*

fun EditText.showKeyboard() {
    post {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

interface IngredientListListener {
    fun lastItemRemoved()
    fun focusedItemChanged(position: Int)
}

class IngredientAdapter(val ingredients: ArrayList<String>, val context: Context, val listener: IngredientListListener, var focusedItemPosition: Int) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredient: EditText = itemView.ingredient

        init {
            ingredient.setOnKeyListener { view, keyCode, keyEvent -> handleKeyClick(keyEvent, keyCode, adapterPosition, view.ingredient) }
            ingredient.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    ingredients[adapterPosition] = text.toString()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
            ingredient.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && adapterPosition != RecyclerView.NO_POSITION) {
                    focusedItemPosition = adapterPosition
                    listener.focusedItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.ingredient_edit_item, parent, false))

    override fun onBindViewHolder(viewHolder: IngredientViewHolder, position: Int) {
        viewHolder.ingredient.apply {
            setText(ingredients[position])
            if (focusedItemPosition != RecyclerView.NO_POSITION && focusedItemPosition == position) {
                requestFocus()
                setSelection(text.length)
                showKeyboard()
            }
        }
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
        focusedItemPosition = position + 1
        notifyItemInserted(position + 1)
        return true
    }

    private fun backspaceClicked(position: Int, view: EditText) = if (view.text.isEmpty() && position < itemCount) {
        ingredients.removeAt(position)
        if (itemCount == 0) {
            listener.lastItemRemoved()
        } else {
            focusedItemPosition = position - 1
            notifyItemRemoved(position)
            notifyItemChanged(focusedItemPosition)
        }
        true
    } else {
        false
    }
}