package ru.buylist.presentation.recipe_detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipe_detail.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipeDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.recipe_add_edit.RecipeHeaderAdapter
import ru.buylist.presentation.recipe_detail.data.RecipeShareData
import ru.buylist.utils.EventObserver
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar
import java.lang.StringBuilder


/**
 * Recipe detail screen.
 */

class RecipeDetailFragment : BaseFragment<FragmentRecipeDetailBinding>() {

    private val args: RecipeDetailFragmentArgs by navArgs()

    private val viewModel: RecipeDetailViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_recipe_detail

    override fun setupBindings(binding: FragmentRecipeDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start(args.recipeId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupAdapter()
        setupNavigation()
        setupSnackbar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                viewModel.share()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupNavigation() {
        viewModel
            .editEvent
            .observe(
                viewLifecycleOwner,
                EventObserver {
                    val action = RecipeDetailFragmentDirections
                        .actionRecipeDetailFragmentToRecipeAddEditFragment(
                            args.recipeId,
                            args.recipeTitle
                        )
                    findNavController().navigate(action)
                }
            )

        viewModel
            .shareRecipe
            .observe(viewLifecycleOwner, EventObserver(::shareRecipe))
    }

    private fun shareRecipe(recipeShareData: RecipeShareData) {
        val data = StringBuilder().apply {
            // title
            append(
                args.recipeTitle.ifBlank {
                    getString(R.string.recipe_title_share)
                }
            )
            append("\n")
            append("\n")

            // ingredients
            append(getString(R.string.label_recipe_ingredient))
            append("\n")
            recipeShareData.ingredients.forEachIndexed { index, ingredient ->
                append(ingredient)
                if (index != recipeShareData.ingredients.lastIndex) {
                    append("\n")
                }
            }
            append("\n")

            // cookingSteps
            append(getString(R.string.label_cooking_steps))
            append("\n")
            recipeShareData.cookingSteps.forEachIndexed { index, step ->
                append(step)
                if (index != recipeShareData.cookingSteps.lastIndex) {
                    append("\n")
                }
            }
        }.toString()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "text/plain"
        }

        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.label_share)
            )
        )
    }

    private fun setupAdapter() {
        val generalInfoAdapter = RecipeDetailGeneralInfoAdapter()
        val itemsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.label_recipe_ingredient))
        val itemsAdapter = RecipeDetailItemsAdapter()
        val stepsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.label_cooking_steps))
        val stepsAdapter = RecipeDetailStepsAdapter()
        val concatAdapter = ConcatAdapter(
            generalInfoAdapter, itemsHeaderAdapter, itemsAdapter, stepsHeaderAdapter, stepsAdapter
        )
        recycler.adapter = concatAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }


}