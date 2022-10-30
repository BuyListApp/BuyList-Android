package ru.buylist.presentation.recipes

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipes.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipesBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.data.RecipeSortKind
import ru.buylist.utils.EventObserver
import ru.buylist.utils.getRecipesSortKind
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar

class RecipesFragment : BaseFragment<FragmentRecipesBinding>() {

    private val viewModel: RecipesViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_recipes

    override fun setupBindings(binding: FragmentRecipesBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupAdapter()
        setupNavigation()
        setupSnackbar()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let { viewModel.search(it) }
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.search(null)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_sort -> {
                setupSortRecipesPopupMenu(requireActivity().findViewById(R.id.menu_sort))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupNavigation() {
        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailFragment(
                recipe.id,
                recipe.title
            )
            findNavController().navigate(action)
        })

        viewModel.newRecipeEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipesFragmentDirections.actionRecipesFragmentToRecipeAddEditFragment(
                recipe.id, recipe.toolbarTitle
            )
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val recipeAdapter = RecipesAdapter(viewModel)
        recycler.adapter = recipeAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }

    private fun setupSortRecipesPopupMenu(item: View) {
        PopupMenu(requireContext(), item).apply {
            menuInflater.inflate(R.menu.recipes_popup_menu, menu)
            val selectedItem = when (getRecipesSortKind()) {
                RecipeSortKind.ALPHABETICALLY ->
                    menu.findItem(R.id.menu_recipes_by_alphabetically)
                RecipeSortKind.CATEGORY ->
                    menu.findItem(R.id.menu_recipes_by_category)
                RecipeSortKind.COOKING_TIME ->
                    menu.findItem(R.id.menu_recipes_by_cooking_time)
                RecipeSortKind.DATE_OF_CREATION ->
                    menu.findItem(R.id.menu_recipes_by_date_of_creation)
            }
            selectedItem.isChecked = !selectedItem.isChecked

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_recipes_by_alphabetically ->
                        viewModel.sortRecipesBy(RecipeSortKind.ALPHABETICALLY)
                    R.id.menu_recipes_by_category ->
                        viewModel.sortRecipesBy(RecipeSortKind.CATEGORY)
                    R.id.menu_recipes_by_cooking_time ->
                        viewModel.sortRecipesBy(RecipeSortKind.COOKING_TIME)
                    R.id.menu_recipes_by_date_of_creation ->
                        viewModel.sortRecipesBy(RecipeSortKind.DATE_OF_CREATION)
                }
                item.isChecked = !item.isChecked
                true
            }
            show()
        }
    }

}