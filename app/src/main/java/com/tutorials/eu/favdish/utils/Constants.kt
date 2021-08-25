package com.tutorials.eu.favdish.utils

object Constants {

    const val NOTIFICATION_NAME="FAV_DISH"
    const val NOTIFICATION_CHANNEL="FAV_CHANNEL"
    const val NOTIFICATION_ID="11"
    const val FILTER_SELECTION="FilterSelection"
    const val All_ITEMS="All Items"
    const val DISH_TYPE="DishType"
    const val DISH_CATEGORY="DishCategory"
    const val DISH_COOKING_TIME="DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL="Local"
    const val DISH_IMAGE_SOURCE_REMOTE="Remote"

    const val EXTRA_DISH_DETAIL="DishDetails"

    const val API_BASE_URL="https://api.spoonacular.com/"
    const val API_ENDPOINT="recipes/random"
    const val API_KEY_VALUE="24d12d3201f944bd9a26391e997ce140"
    const val API_KEY="apiKey"

    const val LIMIT_LICENSE="limitLicense"
    const val LIMIT_LICENSE_VALUE=true

    const val TAGS="tags"
    const val TAGS_VALUE="vegetarian,dessert"

    const val NUMBER="number"
    const val NUMBER_VALUE=1








    fun dishTypes():ArrayList<String>{

        val list=ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snacks")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side dish")
        list.add("Dessert")
        list.add("Other")

        return list
    }
    fun dishCategories():ArrayList<String>{

        val list=ArrayList<String>()
        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Dessert")
        list.add("Drinks")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Wraps")
        list.add("Other")

        return list
    }

    fun dishCookingTime():ArrayList<String>{

        val list=ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")

        return list
    }


}