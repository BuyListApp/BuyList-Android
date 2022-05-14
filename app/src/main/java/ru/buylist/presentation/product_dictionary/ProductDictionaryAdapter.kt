package ru.buylist.presentation.product_dictionary

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.wrappers.GlobalItemWrapper
import ru.buylist.databinding.ItemProductDictionaryBinding
import ru.buylist.presentation.GenericViewHolder
import ru.buylist.utils.hideKeyboard


/**
 * Adapter for the products on product dictionary screen.
 */
class ProductDictionaryAdapter(
        private val viewModel: ProductDictionaryViewModel
) : ListAdapter<GlobalItemWrapper, GenericViewHolder>(ProductDictionaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemProductDictionaryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_product_dictionary,
            parent, false
        )
        return ProductDictionaryHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun getListener(
        context: Context,
        btnMore: View,
        fieldName: EditText
    ): ProductDictionaryItemListener {
        return object : ProductDictionaryItemListener {

            override fun onButtonMoreClick(globalItemWrapper: GlobalItemWrapper) {
                PopupMenu(context, btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                viewModel.edit(globalItemWrapper)
                                fieldName.requestFocus()
                            }
                            R.id.delete -> viewModel.delete(globalItemWrapper)
                        }
                        true
                    }
                    show()
                }
            }

            override fun onButtonSaveClick(globalItemWrapper: GlobalItemWrapper) {
                viewModel.saveEditedData(
                    wrapper = globalItemWrapper,
                    newName = fieldName.text.toString()
                )
                fieldName.hideKeyboard()
            }
        }
    }



    /**
     * ViewHolder
     */
    private inner class ProductDictionaryHolder(private val binding: ItemProductDictionaryBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = getListener(
                context = itemView.context,
                btnMore = binding.btnMore,
                fieldName = binding.fieldItemTitle
            )
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(item.item.color))
        }

    }
}


/**
 * DiffUtil
 */
class ProductDictionaryDiffCallback : DiffUtil.ItemCallback<GlobalItemWrapper>() {
    override fun areItemsTheSame(oldItem: GlobalItemWrapper, newItem: GlobalItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: GlobalItemWrapper, newItem: GlobalItemWrapper): Boolean {
        return oldItem == newItem
    }
}

/**
 * Callbacks
 */
interface ProductDictionaryItemListener {

    fun onButtonMoreClick(globalItemWrapper: GlobalItemWrapper)

    fun onButtonSaveClick(globalItemWrapper: GlobalItemWrapper)

}