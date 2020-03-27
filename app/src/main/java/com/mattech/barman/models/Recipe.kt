package com.mattech.barman.models

data class Recipe(val id: Int, val name: String, val description: String, val ingredients: ArrayList<String>)