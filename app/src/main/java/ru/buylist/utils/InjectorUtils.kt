package ru.buylist.utils

import androidx.savedstate.SavedStateRegistryOwner
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.buyList.BuyListsRepository
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.items.GlobalItemsRepository
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsRepository
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesRepository
import ru.buylist.presentation.ViewModelFactory
import ru.buylist.presentation.import_and_export_data.IImportExportDataProvider
import ru.buylist.presentation.import_and_export_data.ImportExportDataProvider

object InjectorUtils {

    fun provideViewModel(owner: SavedStateRegistryOwner) = ViewModelFactory(
        getBuyListsRepository(),
        getPatternsRepository(),
        getRecipesRepository(),
        getGlobalItemsRepository(),
        getImportExportDataProvider(),
        owner
    )

    private fun getImportExportDataProvider(): IImportExportDataProvider {
        return ImportExportDataProvider.getInstance(
            buyListsRepository = getBuyListsRepository(),
            patternsRepository = getPatternsRepository(),
            recipesRepository = getRecipesRepository(),
            globalItemRepository = getGlobalItemsRepository()
        )
    }

    private fun getBuyListsRepository(): BuyListsDataSource {
        return BuyListsRepository.getInstance(
            BuyListApp.get().getDatabase().buyListDao(),
            BuyListApp.get().getDatabase().globalItemDao()
        )
    }

    private fun getPatternsRepository(): PatternsDataSource {
        return PatternsRepository.getInstance(
            BuyListApp.get().getDatabase().patternDao(),
            BuyListApp.get().getDatabase().globalItemDao()
        )
    }

    private fun getRecipesRepository(): RecipesDataSource {
        return RecipesRepository.getInstance(
            BuyListApp.get().getDatabase().recipeDao(),
            BuyListApp.get().getDatabase().globalItemDao()
        )
    }

    private fun getGlobalItemsRepository(): GlobalItemsDataSource {
        return GlobalItemsRepository.getInstance(BuyListApp.get().getDatabase().globalItemDao())
    }
}