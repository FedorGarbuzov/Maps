package ru.netology.maps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.maps.dto.Marker

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDto() = Marker(
        id,
        title,
        latitude,
        longitude
    )
}

fun Marker.toEntity() = MarkerEntity(
    id,
    title,
    latitude,
    longitude
)

fun List<MarkerEntity>.toDto(): List<Marker> = map(MarkerEntity::toDto)
fun List<Marker>.toEntity(): List<MarkerEntity> = map(Marker::toEntity)