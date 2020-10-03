package com.mattech.barman.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mattech.barman.R
import kotlinx.android.synthetic.main.fragment_list_page.*

interface IngredientsActionListener {
    fun addIngredients()
}

class ListPageFragment : Fragment() {
    private lateinit var listener: IngredientsActionListener

    companion object {
        const val INGREDIENTS_ARG_KEY = "text"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as IngredientsActionListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(INGREDIENTS_ARG_KEY) }?.let {
            val ingredients: List<String> = it.getStringArrayList(INGREDIENTS_ARG_KEY) ?: listOf()
            ingredients_list.adapter = ArrayAdapter<String>(context,
                    R.layout.ingredient_show_item,
                    ingredients
            )
            if (ingredients.isEmpty()) {
                ingredients_list.visibility = View.GONE
                add_ingredients.visibility = View.VISIBLE
                add_ingredients.setOnClickListener { listener.addIngredients() }
            } else {
                ingredients_list.visibility = View.VISIBLE
                add_ingredients.visibility = View.GONE
            }
        }
    }
}