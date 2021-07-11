package ru.netology.maps.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.netology.maps.dto.Marker
import ru.netology.maps.ui.EditMarkerFragment
import ru.netology.maps.ui.MapsFragment.Companion.editArg
import ru.netology.maps.ui.MapsFragment.Companion.markerArg

object AndroidUtils {
    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}