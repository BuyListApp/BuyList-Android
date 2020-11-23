package ru.buylist.presentation.word_tips_adapter

import android.graphics.Color
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem


object WordTipsBindings {

    @BindingAdapter("app:wordTips")
    @JvmStatic
    fun setWordTips(recycler: RecyclerView, tags: List<GlobalItem>?) {
        tags?.let {
            (recycler.adapter as WordTipsAdapter).submitList(tags)
        }
    }

    @BindingAdapter("app:wordTipColor")
    @JvmStatic
    fun setWordTipBackgroundColor(card: CardView, color: String) {
        card.setCardBackgroundColor(Color.parseColor(color))
    }
}