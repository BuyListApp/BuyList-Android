package ru.buylist.data.repositories.recipe

import androidx.lifecycle.LiveData
import ru.buylist.data.Result
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.entity.Recipe

interface RecipesDataSource {

    suspend fun saveRecipe(recipe: Recipe)

    suspend fun updateRecipe(recipe: Recipe)

    suspend fun deleteRecipe(recipe: Recipe)

    suspend fun deleteSelectedRecipes(recipes: List<Recipe>)

    suspend fun deleteAllRecipes()

    fun observeRecipes(): LiveData<Result<List<Recipe>>>

    suspend fun getRecipes(): Result<List<Recipe>>

    suspend fun getRecipe(recipeId: Long): Result<Recipe>

    fun observeRecipe(recipeId: Long): LiveData<Result<Recipe>>

    suspend fun getTags(): Result<List<GlobalItem>>
}