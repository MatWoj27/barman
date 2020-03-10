package com.mattech.barman

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mattech.barman.utils.CircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_list.layoutManager = LinearLayoutManager(this)
        main_list.adapter = DrinksAdapter(getDrinks(), this)
        main_list.setHasFixedSize(true)
        main_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                outRect.top = if (position == 0) 16 else 0
                outRect.bottom = 16
            }
        })
        presetNavigationHeader()
    }


    private fun getDrinks(): List<Drink> {
        val drinks: ArrayList<Drink> = ArrayList()
        drinks.add(Drink("Cosmopolitan", 1))
        drinks.add(Drink("Mojito", 2))
        drinks.add(Drink("Sex on the beach", 3))
        return drinks
    }

    private fun presetNavigationHeader() {
        val header = drawer.getHeaderView(0)
        val userPhoto = header.user_photo
        Picasso.with(this).load("https://api.adorable.io/avatars/128").transform(CircleTransformation()).into(userPhoto)
    }
}
