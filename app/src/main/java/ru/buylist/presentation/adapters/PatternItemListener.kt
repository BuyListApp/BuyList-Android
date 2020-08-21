package ru.buylist.presentation.adapters

import ru.buylist.data.entity.wrappers.PatternWrapper

interface PatternItemListener {

    fun onPatternClicked(wrapper: PatternWrapper)

    fun onButtonMoreClick(wrapper: PatternWrapper)

    fun onButtonSaveClick(wrapper: PatternWrapper)
}