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
            ingredient.apply {
                setOnKeyListener { view, keyCode, keyEvent ->
                    if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                        backspaceClicked(adapterPosition, view as EditText)
                    } else {
                        false
                    }
                }
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(text: Editable?) {}

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                        text?.let {
                            if (count > before && it[start] == '\n') {
                                val finalText = ingredient.text.substring(0, start)
                                ingredient.setText(finalText)
                                val movedText = if (finalText.length != it.length - 1) it.substring(start + 1, it.length) else ""
                                enterClicked(adapterPosition, movedText)
                            } else {
                                ingredients[adapterPosition] = text.toString()
                            }
                        }
                    }
                })
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && adapterPosition != RecyclerView.NO_POSITION) {
                        focusedItemPosition = adapterPosition
                        listener.focusedItemChanged(adapterPosition)
                    }
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

    private fun enterClicked(position: Int, movedText: String) {
        ingredients.add(position + 1, movedText)
        focusedItemPosition = position + 1
        notifyItemInserted(position + 1)
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