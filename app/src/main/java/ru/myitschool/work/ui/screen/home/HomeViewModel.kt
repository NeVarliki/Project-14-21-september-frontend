package ru.myitschool.work.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.repo.BookRepository
import ru.myitschool.work.data.source.ApiException
import ru.myitschool.work.domain.auth.LogoutUseCase
import ru.myitschool.work.domain.book.BookPlaceUseCase
import ru.myitschool.work.domain.book.BookRoomTodayUseCase
import ru.myitschool.work.domain.book.FreeBookingUseCase
import ru.myitschool.work.domain.book.GetFreePlacesUseCase
import ru.myitschool.work.domain.main.GetMainDataUseCase
import ru.myitschool.work.ui.components.AlertKind
import ru.myitschool.work.ui.nav.AuthScreenDestination

class HomeViewModel : ViewModel() {
    private val getMainData = GetMainDataUseCase(BookRepository)
    private val getFreePlaces = GetFreePlacesUseCase(BookRepository)
    private val bookPlace = BookPlaceUseCase(BookRepository)
    private val bookRoom = BookRoomTodayUseCase(BookRepository)
    private val freeBooking = FreeBookingUseCase(BookRepository)
    private val logout = LogoutUseCase(AuthRepository)

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<HomeAction>()
    val actionFlow: SharedFlow<HomeAction> = _actionFlow

    init {
        refresh()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> refresh()
            HomeIntent.Logout -> viewModelScope.launch {
                logout()
                _actionFlow.emit(HomeAction.Open(AuthScreenDestination))
            }
            is HomeIntent.SelectTab -> _uiState.update { it.copy(tab = intent.tab) }
            is HomeIntent.SelectDay -> _uiState.update { it.copy(selectedDay = intent.index) }
            is HomeIntent.BookPlace -> {
                val date = _uiState.value.days.getOrNull(_uiState.value.selectedDay)?.date ?: return
                mutate(intent.placeName) { bookPlace(date, intent.placeId) }
            }
            is HomeIntent.BookRoom -> mutate(intent.placeName) { bookRoom(intent.placeId) }
            HomeIntent.AskFree -> _uiState.update { it.copy(alert = AlertKind.Cancel) }
            HomeIntent.ConfirmFree -> mutate(null) { freeBooking() }
            HomeIntent.DismissAlert -> _uiState.update { it.copy(alert = null) }
        }
    }

    private fun mutate(successPlace: String?, block: suspend () -> Result<Unit>) {
        if (_uiState.value.busy) return
        viewModelScope.launch {
            _uiState.update { it.copy(busy = true, alert = null) }
            block().fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(busy = false, alert = successPlace?.let { p -> AlertKind.Success(p) })
                    }
                    load(showLoading = false)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            busy = false,
                            alert = when (error) {
                                is ApiException.NoConnection -> AlertKind.NoInternet
                                else -> AlertKind.Error(error.message ?: "Ошибка")
                            }
                        )
                    }
                }
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch { load(showLoading = true) }
    }

    private suspend fun load(showLoading: Boolean) {
        if (showLoading) _uiState.update { it.copy(loading = true, error = null) }
        val info = viewModelScope.async { getMainData() }
        val places = viewModelScope.async { getFreePlaces() }
        val infoResult = info.await()
        val placesResult = places.await()

        var offline = false
        var unauthorized = false
        var error: String? = null

        infoResult.onSuccess { cached ->
            offline = offline || cached.fromCache
            _uiState.update {
                it.copy(
                    name = cached.value.name,
                    photoUrl = cached.value.photoUrl,
                    myBookings = cached.value.book
                        .map { b -> HomeState.Booking(b.date, b.place) }
                        .toPersistentList()
                )
            }
        }.onFailure { e ->
            when (e) {
                is ApiException.NoConnection -> offline = true
                is ApiException.Unauthorized -> unauthorized = true
                is ApiException.Forbidden -> Unit
                else -> error = e.message
            }
        }
        placesResult.onSuccess { cached ->
            offline = offline || cached.fromCache
            _uiState.update { state ->
                val days = cached.value.map { d ->
                    HomeState.Day(
                        date = d.date,
                        places = d.places.map { p -> HomeState.Place(p.id, p.name) }.toPersistentList()
                    )
                }.toPersistentList()
                state.copy(
                    days = days,
                    selectedDay = state.selectedDay.coerceIn(0, (days.size - 1).coerceAtLeast(0))
                )
            }
        }.onFailure { e ->
            when (e) {
                is ApiException.NoConnection -> offline = true
                is ApiException.Unauthorized -> unauthorized = true
                else -> error = error ?: e.message
            }
        }

        if (unauthorized) {
            logout()
            _actionFlow.emit(HomeAction.Open(AuthScreenDestination))
            return
        }
        val hadNothing = _uiState.value.name.isEmpty() && _uiState.value.days.isEmpty()
        _uiState.update {
            it.copy(
                loading = false,
                offline = offline,
                error = error,
                alert = if (offline && hadNothing && it.alert == null) AlertKind.NoInternet else it.alert
            )
        }
    }
}
