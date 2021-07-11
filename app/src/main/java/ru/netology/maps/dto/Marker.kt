package ru.netology.maps.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Marker(
    val id: Int = 0,
    val title: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Parcelable