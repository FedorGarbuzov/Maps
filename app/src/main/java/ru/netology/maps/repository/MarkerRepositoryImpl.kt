package ru.netology.maps.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.maps.dao.MarkerDao
import ru.netology.maps.dto.Marker
import ru.netology.maps.entity.toDto
import ru.netology.maps.entity.toEntity

class MarkerRepositoryImpl(
    private val dao: MarkerDao
): MarkerRepository
 {
    override val data: Flow<List<Marker>> =
        dao.getAll()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

     override suspend fun save(marker: Marker) {
         try {
             dao.insert(marker.toEntity())
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

     override suspend fun removeById(id: Int) {
         try{
             dao.removeById(id)
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }
}