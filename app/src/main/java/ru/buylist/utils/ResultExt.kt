package ru.buylist.utils

import ru.buylist.data.Result


fun <T: Any> Result<T>.getSuccessOrNull(): T? {
    return when(this) {
        is Result.Success -> this.data
        is Result.Error -> null
        is Result.Loading -> null
    }
}