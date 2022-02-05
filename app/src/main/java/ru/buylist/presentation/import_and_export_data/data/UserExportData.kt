package ru.buylist.presentation.import_and_export_data.data

import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.entity.Pattern
import ru.buylist.data.entity.Recipe

data class UserExportData(
    val buyLists: List<BuyList>,
    val patterns: List<Pattern>,
    val recipes: List<Recipe>,
    val dictionaryProducts: List<GlobalItem>
)