package com.mattech.barman.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mattech.barman.fragments.ListPageFragment
import com.mattech.barman.fragments.TextPageFragment
import com.mattech.barman.models.Recipe

class RecipeContentPageAdapter(fragmentActivity: FragmentActivity, private val recipe: Recipe) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            TextPageFragment().apply {
                arguments = Bundle().apply {
                    putString(TextPageFragment.TEXT_ARG_KEY, recipe.description)
                }
            }
        } else {
            ListPageFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ListPageFragment.INGREDIENTS_ARG_KEY, recipe.ingredients)
                }
            }
        }
    }
}