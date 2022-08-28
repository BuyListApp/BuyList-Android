package ru.buylist.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <X, Y> combineLatest(
    first: LiveData<X>,
    second: LiveData<Y>
): LiveData<Pair<X?, Y?>> {
    return MediatorLiveData<Pair<X?, Y?>>().apply {
        var lastFirst: X? = null
        var lastSecond: Y? = null

        addSource(first) {
            lastFirst = it
            value = lastFirst to lastSecond
        }

        addSource(second) {
            lastSecond = it
            value = lastFirst to lastSecond
        }
    }
}