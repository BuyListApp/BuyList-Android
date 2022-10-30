package ru.buylist.presentation.recipes

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Recipe
import ru.buylist.data.wrappers.RecipeWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.R
import ru.buylist.presentation.data.RecipeSortKind
import ru.buylist.presentation.data.SnackbarData
import ru.buylist.utils.combineLatest
import ru.buylist.utils.getRecipesSortKind
import ru.buylist.utils.saveRecipesSortKind

class RecipesViewModel(private val repository: RecipesDataSource) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)
    private val _sortKind = MutableLiveData(getRecipesSortKind())

    private val _recipes: LiveData<List<RecipeWrapper>> = combineLatest(
        _forceUpdate,
        _sortKind
    ).switchMap { (forceUpdate, sortKind) ->
        if (forceUpdate == true) {
            TODO("Load recipes from remote data source.")
        }
        repository
            .observeRecipes()
            .distinctUntilChanged()
            .map { result ->
                loadRecipes(
                    recipesResult = result,
                    sortKind = sortKind
                )
            }
    }

    val recipes: LiveData<List<RecipeWrapper>> = _recipes

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_recipes) {
        it.isEmpty()
    }

    val fabIsShown = MutableLiveData<Boolean>(true)

    private val _snackbarText = MutableLiveData<Event<SnackbarData>>()
    val snackbarText: LiveData<Event<SnackbarData>> = _snackbarText

    private val _newRecipeEvent = MutableLiveData<Event<Recipe>>()
    val newRecipeEvent: LiveData<Event<Recipe>> = _newRecipeEvent

    private val _detailsEvent = MutableLiveData<Event<Recipe>>()
    val detailsEvent: LiveData<Event<Recipe>> = _detailsEvent



    fun addNewRecipe() {
        _newRecipeEvent.value = Event(Recipe(0L))
    }

    fun edit(wrapper: RecipeWrapper) {
        _newRecipeEvent.value = Event(wrapper.recipe)
    }

    fun delete(wrapper: RecipeWrapper) = viewModelScope.launch {
        repository.deleteRecipe(wrapper.recipe)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = (dy <= 0)
    }

    fun showDetail(recipe: Recipe) {
        _detailsEvent.value = Event(recipe)
    }

    fun sortRecipesBy(kind: RecipeSortKind) {
        saveRecipesSortKind(kind)
        _sortKind.value = kind
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(SnackbarData(message))
    }

    private fun getWrappedRecipes(recipes: List<Recipe>): List<RecipeWrapper> {
        val newList = mutableListOf<RecipeWrapper>()
        for ((i, recipe) in recipes.withIndex()) {
            val wrappedRecipe = RecipeWrapper(recipe.copy(), i)
            newList.add(wrappedRecipe)
        }
        return newList
    }

    private fun loadRecipes(
        recipesResult: Result<List<Recipe>>,
        sortKind: RecipeSortKind?
    ): List<RecipeWrapper> {
        return if (recipesResult is Success) {
            getWrappedRecipes(recipesResult.data).sortedWith(
                compareBy { wrapper ->
                    when (sortKind) {
                        RecipeSortKind.ALPHABETICALLY -> wrapper.recipe.title
                        RecipeSortKind.CATEGORY -> wrapper.recipe.category
                        RecipeSortKind.COOKING_TIME -> wrapper.recipe.cookingTime
                        RecipeSortKind.DATE_OF_CREATION, null -> wrapper.recipe.id
                    }
                }
            )
        } else {
            showSnackbarMessage(R.string.snackbar_recipes_loading_error)
            emptyList()
        }
    }
}