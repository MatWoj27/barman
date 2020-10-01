package com.mattech.barman.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.mattech.barman.R
import kotlinx.android.synthetic.main.fragment_list_page.*

class ListPageFragment : Fragment() {

    companion object {
        const val INGREDIENTS_ARG_KEY = "text"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(INGREDIENTS_ARG_KEY) }?.let {
            ingredients_list.adapter = ArrayAdapter<String>(context,
                    R.layout.ingredient_show_item,
                    it.getStringArrayList(INGREDIENTS_ARG_KEY) ?: listOf())
        }
    }
}