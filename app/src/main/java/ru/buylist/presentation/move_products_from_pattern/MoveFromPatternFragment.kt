package ru.buylist.presentation.move_products_from_pattern

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_move_from_pattern.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.buylist.R
import ru.buylist.databinding.FragmentMoveFromPatternBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar

class MoveFromPatternFragment : BaseFragment<FragmentMoveFromPatternBinding>() {

    private val args: MoveFromPatternFragmentArgs by navArgs()

    private val viewModel: MoveFromPatternViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_move_from_pattern

    override fun setupBindings(binding: FragmentMoveFromPatternBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.start(args.buyListId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.setNavigationIcon(R.drawable.ic_close)
        setupAdapter()
        setupSnackbar()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupAdapter() {
        val patternsAdapter = MoveFromPatternAdapter(viewModel)
        recycler_patterns.adapter = patternsAdapter
    }
}