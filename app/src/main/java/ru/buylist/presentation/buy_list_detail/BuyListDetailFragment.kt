package ru.buylist.presentation.buy_list_detail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_buy_list_detail.*
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.databinding.FragmentBuyListDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.circle_adapter.CircleItemClickListener
import ru.buylist.presentation.circle_adapter.CirclesAdapter
import ru.buylist.presentation.word_tips_adapter.WordTipsAdapter
import ru.buylist.presentation.word_tips_adapter.WordTipsListener
import ru.buylist.utils.*

class BuyListDetailFragment : BaseFragment<FragmentBuyListDetailBinding>() {

    private val args: BuyListDetailFragmentArgs by navArgs()

    private val viewModel: BuyListDetailViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_buy_list_detail

    private lateinit var circlesAdapter: CirclesAdapter

    override fun setupBindings(binding: FragmentBuyListDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.start(args.buyListId, resources.getStringArray(R.array.category_color).toList())
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
            .addProductEvent
            .observe(
                viewLifecycleOwner,
                EventObserver { expand(fab_add, fab_menu, null) }
            )

        viewModel
            .newProductEvent
            .observe(
                viewLifecycleOwner,
                EventObserver { expand(fab_menu, layout_new_item, field_name) }
            )

        viewModel
            .productsFromPatternEvent
            .observe(
                viewLifecycleOwner,
                EventObserver {
                    val action = BuyListDetailFragmentDirections
                        .actionBuyListDetailFragmentToMoveProductsFromPatternFragment(args.buyListId)
                    findNavController().navigate(action)
                }
            )

        viewModel
            .productsFromRecipeEvent
            .observe(
                viewLifecycleOwner,
                EventObserver {
                    val action = BuyListDetailFragmentDirections
                        .actionBuyListDetailFragmentToMoveProductsFromRecipeFragment(args.buyListId)
                    findNavController().navigate(action)
                }
            )

        viewModel
            .productAddedEvent
            .observe(
                viewLifecycleOwner,
                EventObserver { minimize(fab_menu, fab_add, field_name) }
            )

        viewModel
            .saveProductEvent
            .observe(
                viewLifecycleOwner,
                EventObserver { field_name.requestFocus() }
            )

        viewModel
            .shareBuyList
            .observe(viewLifecycleOwner, EventObserver(::shareBuyList))
    }

    private fun shareBuyList(products: List<String>) {
        val data = StringBuilder().apply {
            append(
                args.buyListTitle.ifBlank {
                    getString(R.string.buylist_title_share)
                }
            )
            append("\n")
            append("\n")
            products.forEachIndexed { index, product ->
                append(product)
                if (index != products.lastIndex) {
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

    private fun expand(start: View, end: View, field: EditText?) {
        val transition = buildContainerTransform().apply {
            startView = start
            endView = end
            addTarget(end)
        }

        TransitionManager.beginDelayedTransition(
            requireActivity().findViewById(android.R.id.content),
            transition
        )
        end.visibility = View.VISIBLE
        shadow_view.visibility = View.VISIBLE
        start.visibility = View.GONE
        field?.showKeyboard()
    }

    private fun minimize(start: View, end: View, field: EditText) {
        val transition = buildContainerTransform().apply {
            startView = start
            endView = end
            addTarget(fab_add)
        }
        TransitionManager.beginDelayedTransition(coordinator_layout, transition)
        start.visibility = View.GONE
        shadow_view.visibility = View.GONE
        layout_new_item.visibility = View.GONE
        end.visibility = View.VISIBLE
        field.hideKeyboard()
    }

    private fun buildContainerTransform() =
        MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
            duration = 500
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
            interpolator = FastOutSlowInInterpolator()
        }

    private fun isLastCircleVisible(circlesAdapter: CirclesAdapter): Boolean {
        val layoutManager: LinearLayoutManager =
            recycler_circles.layoutManager as LinearLayoutManager
        val position = layoutManager.findLastVisibleItemPosition()
        return (position >= circlesAdapter.itemCount - 1)
    }

    private fun isFirstCircleVisible(): Boolean {
        val layoutManager: LinearLayoutManager =
            recycler_circles.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        return position > 0
    }

    private fun setupAdapter() {
        val itemsAdapter = BuyListDetailAdapter(viewModel)
        recycler_items.adapter = itemsAdapter

        recycler_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })

        circlesAdapter = CirclesAdapter(circlesCallback)
        recycler_circles.apply { adapter = circlesAdapter }

        recycler_circles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideArrows(
                    isFirstCircleVisible(),
                    isLastCircleVisible(circlesAdapter)
                )
            }
        })

        val wordTipsAdapter = WordTipsAdapter(wordTipCallback)
        val flexManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.CENTER
        }

        recycler_word_tips.apply {
            layoutManager = flexManager
            adapter = wordTipsAdapter
        }
    }

    private val circlesCallback = object : CircleItemClickListener {
        override fun onCircleClick(circleWrapper: CircleWrapper) {
            viewModel.updateCircle(circleWrapper)
        }
    }

    private val wordTipCallback = object : WordTipsListener {

        override fun onWordTipClick(wordTip: GlobalItem) {
            viewModel.setProductInfoByWordTip(wordTip)
        }
    }


}