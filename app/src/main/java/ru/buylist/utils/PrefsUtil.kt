package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp
import ru.buylist.presentation.data.RecipeSortKind

private val prefs by lazy {
    BuyListApp.get().getSharedPreferences("BuyListPrefs", Context.MODE_PRIVATE)
}

private const val FIRST_START = "first start"
private const val RECIPES_SORT_KIND = "recipes_sort_kind"

fun getFirstStart(): Boolean = prefs.getBoolean(FIRST_START, true)

fun changeFirstStart(isFirst: Boolean = false) {
    prefs.edit().putBoolean(FIRST_START, isFirst).apply()
}

fun getRecipesSortKind(): RecipeSortKind = prefs
    .getString(RECIPES_SORT_KIND, RecipeSortKind.DATE_OF_CREATION.name)
    ?.let { RecipeSortKind.valueOf(it) }
    ?: RecipeSortKind.DATE_OF_CREATION

fun saveRecipesSortKind(kind: RecipeSortKind) {
    prefs
        .edit()
        .putString(RECIPES_SORT_KIND, kind.name)
        .apply()
}