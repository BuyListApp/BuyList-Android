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

fun <X, Y, Z> combineLatest(
    first: LiveData<X>,
    second: LiveData<Y>,
    third: LiveData<Z>
): LiveData<Triple<X?, Y?, Z?>> {
    return MediatorLiveData<Triple<X?, Y?, Z?>>().apply {
        var lastFirst: X? = null
        var lastSecond: Y? = null
        var lastThird: Z? = null

        addSource(first) {
            lastFirst = it
            value = Triple(lastFirst, lastSecond, lastThird)
        }

        addSource(second) {
            lastSecond = it
            value = Triple(lastFirst, lastSecond, lastThird)
        }

        addSource(third) {
            lastThird = it
            value = Triple(lastFirst, lastSecond, lastThird)
        }
    }
}