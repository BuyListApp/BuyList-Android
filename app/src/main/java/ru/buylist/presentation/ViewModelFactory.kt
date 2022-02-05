package ru.buylist.presentation

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.buy_list_detail.BuyListDetailViewModel
import ru.buylist.presentation.buy_lists.BuyListsViewModel
import ru.buylist.presentation.import_and_export_data.IImportExportDataProvider
import ru.buylist.presentation.import_and_export_data.ImportAndExportDataViewModel
import ru.buylist.presentation.move_products_from_pattern.MoveFromPatternViewModel
import ru.buylist.presentation.move_products_from_recipe.MoveFromRecipeViewModel
import ru.buylist.presentation.pattern_detail.PatternDetailViewModel
import ru.buylist.presentation.patterns.PatternsViewModel
import ru.buylist.presentation.product_dictionary.ProductDictionaryViewModel
import ru.buylist.presentation.recipe_add_edit.RecipeAddEditViewModel
import ru.buylist.presentation.recipe_detail.RecipeDetailViewModel
import ru.buylist.presentation.recipes.RecipesViewModel


/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val buyListsRepository: BuyListsDataSource,
    private val patternsRepository: PatternsDataSource,
    private val recipesRepository: RecipesDataSource,
    private val globalItemRepository: GlobalItemsDataSource,
    private val importExportDataProvider: IImportExportDataProvider,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            // buy lists
            isAssignableFrom(BuyListsViewModel::class.java) ->
                BuyListsViewModel(buyListsRepository)
            isAssignableFrom(BuyListDetailViewModel::class.java) ->
                BuyListDetailViewModel(buyListsRepository)

            // patterns
            isAssignableFrom(PatternsViewModel::class.java) ->
                PatternsViewModel(patternsRepository)
            isAssignableFrom(PatternDetailViewModel::class.java) ->
                PatternDetailViewModel(patternsRepository)

            // recipes
            isAssignableFrom(RecipesViewModel::class.java) ->
                RecipesViewModel(recipesRepository)
            isAssignableFrom(RecipeAddEditViewModel::class.java) ->
                RecipeAddEditViewModel(recipesRepository)
            isAssignableFrom(RecipeDetailViewModel::class.java) ->
                RecipeDetailViewModel(recipesRepository)

            // move from pattern/recipe
            isAssignableFrom(MoveFromPatternViewModel::class.java) ->
                MoveFromPatternViewModel(buyListsRepository, patternsRepository)
            isAssignableFrom(MoveFromRecipeViewModel::class.java) ->
                MoveFromRecipeViewModel(buyListsRepository, recipesRepository)

            // product dictionary
            isAssignableFrom(ProductDictionaryViewModel::class.java) ->
                ProductDictionaryViewModel(globalItemRepository)

            // import and export data
            isAssignableFrom(ImportAndExportDataViewModel::class.java) ->
                ImportAndExportDataViewModel(importExportDataProvider)

            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
