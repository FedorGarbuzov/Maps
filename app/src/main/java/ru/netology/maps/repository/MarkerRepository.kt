package ru.netology.maps.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.maps.dto.Marker

interface MarkerRepository {
    val data: Flow<List<Marker>>
    suspend fun save(marker: Marker)
    suspend fun removeById(id: Int)
}