package ru.netology.maps.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.netology.maps.db.AppDb
import ru.netology.maps.dto.Marker
import ru.netology.maps.repository.MarkerRepository
import ru.netology.maps.repository.MarkerRepositoryImpl
import ru.netology.maps.utils.SingleLiveEvent

private var emptyMarker = Marker()

class MarkerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MarkerRepository = MarkerRepositoryImpl(
        AppDb.getInstance(application).markersDao()
    )

    val data = repository.data
        .catch { e: Throwable ->
            e.printStackTrace()
            _loadMarkerExceptionEvent.call()
        }

    private val _markerCreatedEvent = SingleLiveEvent<Unit>()
    val markerCreatedEvent: LiveData<Unit>
        get() = _markerCreatedEvent

    private val _loadMarkerExceptionEvent = SingleLiveEvent<Unit>()
    val loadMarkerExceptionEvent: LiveData<Unit>
        get() = _loadMarkerExceptionEvent

    private val _saveMarkerExceptionEvent = SingleLiveEvent<Unit>()
    val saveMarkerExceptionEvent: LiveData<Unit>
        get() = _saveMarkerExceptionEvent

    fun save() {
        emptyMarker.let {
            viewModelScope.launch {
                try {
                    repository.save(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _saveMarkerExceptionEvent.call()
                }
                _markerCreatedEvent.call()
            }
        }
        emptyMarker = Marker()
    }

    fun changeData(title: String, latitude: Double, longitude: Double) {
        emptyMarker = emptyMarker.copy(
            title = title,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun changeTitle(title: String) {
        emptyMarker = emptyMarker.copy(
            title = title
        )
    }

    fun changeLatitude(latitude: Double) {
        emptyMarker = emptyMarker.copy(
            latitude = latitude
        )
    }

    fun changeLongitude(longitude: Double) {
        emptyMarker = emptyMarker.copy(
            longitude = longitude
        )
    }

    fun edit(marker: Marker) {
        emptyMarker = marker
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}