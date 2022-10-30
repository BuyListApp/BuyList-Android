package ru.buylist.data.wrappers

import ru.buylist.data.entity.GlobalItem

data class GlobalItemWrapper(
    var item: GlobalItem,
    var position: Int,
    var isEditable: Boolean = false
)