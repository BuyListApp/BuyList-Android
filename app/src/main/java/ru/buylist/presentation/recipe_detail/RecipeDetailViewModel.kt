package ru.buylist.presentation.recipe_detail

import androidx.lifecycle.*
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.data.SnackbarData
import ru.buylist.presentation.recipe_detail.data.RecipeShareData
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

/**
 * ViewModel for the recipe detail screen.
 */

class RecipeDetailViewModel(private val repository: RecipesDataSource) : ViewModel() {

    private val _recipeId = MutableLiveData<Long>()

    private val _recipe = _recipeId.switchMap { recipeId ->
        repository.observeRecipe(recipeId).map { computeResult(it) }
    }
    val recipe: LiveData<Recipe?> = _recipe

    private val _ingredients = MutableLiveData<List<Item>>()
    val ingredients: LiveData<List<Item>> = _ingredients

    private val _cookingStep = MutableLiveData<List<CookingStep>>()
    val cookingStep: LiveData<List<CookingStep>> = _cookingStep

    val isDataAvailable = _recipe.map { it != null }

    private val _snackbarText = MutableLiveData<Event<SnackbarData>>()
    val snackbarText: LiveData<Event<SnackbarData>> = _snackbarText

    private val _editEvent = MutableLiveData<Event<Unit>>()
    val editEvent: LiveData<Event<Unit>> = _editEvent

    private val _shareRecipe = MutableLiveData<Event<RecipeShareData>>()
    val shareRecipe: LiveData<Event<RecipeShareData>> = _shareRecipe

    val fabIsShown = MutableLiveData(true)


    fun start(recipeId: Long) {
        _recipeId.value = recipeId
    }

    fun share() {
        val ingredientsTemp = _ingredients.value ?: emptyList()
        val ingredientsShareData = ingredientsTemp
            .filter { it.name.isNotBlank() }
            .mapIndexed { index, item ->
                "${index + 1}.${item.name} - ${item.quantity}${item.unit}"
            }

        val cookingStepsTemp = _cookingStep.value ?: emptyList()
        val cookingStepsShareData = cookingStepsTemp
            .filter { it.description.isNotBlank() }
            .mapIndexed { index, item ->
                "${index + 1}.${item.description}"
            }

        if (ingredientsShareData.isEmpty() && cookingStepsShareData.isEmpty()) {
            showSnackbarMessage(R.string.snackbar_recipe_is_empty)
            return
        }
        _shareRecipe.value = Event(
            RecipeShareData(
                ingredients = ingredientsShareData,
                cookingSteps = cookingStepsShareData
            )
        )
    }

    fun editRecipe() {
        _editEvent.value = Event(Unit)
    }

    fun showHideFab(dy: Int) {
        if (isDataAvailable.value == false) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = dy <= 0
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(SnackbarData(message))
    }

    private fun computeResult(recipeResult: Result<Recipe>): Recipe? {
        return if (recipeResult is Success) {
            _ingredients.value = JsonUtils.convertItemsFromJson(recipeResult.data.items)
            _cookingStep.value = JsonUtils
                    .convertCookingStepsFromJson(recipeResult.data.cookingSteps)
            recipeResult.data
        } else {
            showSnackbarMessage(R.string.snackbar_recipe_loading_error)
            null
        }
    }
}