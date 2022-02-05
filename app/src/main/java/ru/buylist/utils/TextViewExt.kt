package ru.buylist.utils

import android.os.Build
import android.text.Html
import android.widget.TextView

fun TextView.setAsHtml(resId: Int) {
    val text = context.getText(resId).toString()
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            this.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        }
        else -> {
            @Suppress("DEPRECATION")
            this.text =Html.fromHtml(text)
        }
    }
}