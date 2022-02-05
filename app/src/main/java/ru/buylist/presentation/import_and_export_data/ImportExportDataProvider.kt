package ru.buylist.presentation.import_and_export_data

import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.import_and_export_data.data.UserExportData
import ru.buylist.utils.getSuccessOrNull

interface IImportExportDataProvider {

    suspend fun exportData(): UserExportData

    suspend fun saveImportedData(data: UserExportData)
}

class ImportExportDataProvider(
    private val buyListsRepository: BuyListsDataSource,
    private val patternsRepository: PatternsDataSource,
    private val recipesRepository: RecipesDataSource,
    private val globalItemRepository: GlobalItemsDataSource
) : IImportExportDataProvider {

    companion object {
        @Volatile
        private var instance: IImportExportDataProvider? = null

        fun getInstance(
            buyListsRepository: BuyListsDataSource,
            patternsRepository: PatternsDataSource,
            recipesRepository: RecipesDataSource,
            globalItemRepository: GlobalItemsDataSource
        ): IImportExportDataProvider {
            return instance ?: synchronized(this) {
                instance ?: ImportExportDataProvider(
                    buyListsRepository = buyListsRepository,
                    patternsRepository = patternsRepository,
                    recipesRepository = recipesRepository,
                    globalItemRepository = globalItemRepository
                )
            }
        }
    }

    override suspend fun exportData(): UserExportData {
        val buyLists = buyListsRepository.getBuyLists().getSuccessOrNull()
        val patterns = patternsRepository.getPatterns().getSuccessOrNull()
        val recipes = recipesRepository.getRecipes().getSuccessOrNull()
        val dictionaryProducts = globalItemRepository.getGlobalItems().getSuccessOrNull()

        return UserExportData(
            buyLists = buyLists ?: emptyList(),
            patterns = patterns ?: emptyList(),
            recipes = recipes ?: emptyList(),
            dictionaryProducts = dictionaryProducts ?: emptyList()
        )
    }

    override suspend fun saveImportedData(data: UserExportData) {
        data.buyLists.forEach {
            buyListsRepository.saveBuyList(it)
        }
        data.patterns.forEach {
            patternsRepository.savePattern(it)
        }
        data.recipes.forEach {
            recipesRepository.saveRecipe(it)
        }
        data.dictionaryProducts.forEach {
            globalItemRepository.saveGlobalItem(it)
        }
    }
}