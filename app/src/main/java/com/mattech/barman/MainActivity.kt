package com.mattech.barman

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_list.layoutManager = LinearLayoutManager(this)
        main_list.adapter = DrinksAdapter(getDrinks(), this)
        main_list.setHasFixedSize(true)
    }

    private fun getDrinks(): List<Drink> {
        val drinks: ArrayList<Drink> = ArrayList()
        drinks.add(Drink("Cosmopolitan", 1))
        drinks.add(Drink("Mojito", 2))
        drinks.add(Drink("Sex on the beach", 3))
        return drinks
    }
}
