package com.mattech.barman

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.mattech.barman.utils.CircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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
        presetNavigationMenu()
        presetDrawerToggle()
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

    private fun presetNavigationMenu() {
        drawer.setNavigationItemSelectedListener { menuItem ->
            val result = when (menuItem.itemId) {
                R.id.long_drinks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.long_drinks), Toast.LENGTH_SHORT).show()
//                   TO DO
                    true
                }
                R.id.short_drinks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.short_drinks), Toast.LENGTH_SHORT).show()
//                   TO DO
                    true
                }
                R.id.shots -> {
                    Toast.makeText(this@MainActivity, getString(R.string.shots), Toast.LENGTH_SHORT).show()
//                    TO DO
                    true
                }
                R.id.snacks -> {
                    Toast.makeText(this@MainActivity, getString(R.string.snacks), Toast.LENGTH_SHORT).show()
//                    TO DO
                    true
                }
                else -> false
            }
            drawer_layout.closeDrawer(drawer)
            result
        }
    }

    private fun presetDrawerToggle() {
        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
                R.string.open_drawer_description, R.string.close_drawer_description)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }
}
