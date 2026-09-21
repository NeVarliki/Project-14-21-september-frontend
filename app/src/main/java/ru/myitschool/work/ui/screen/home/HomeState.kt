package ru.myitschool.work.ui.screen.home

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ru.myitschool.work.ui.components.AlertKind
import ru.myitschool.work.ui.components.HomeTab

data class HomeState(
    val tab: HomeTab = HomeTab.Main,
    val loading: Boolean = true,
    val busy: Boolean = false,
    val offline: Boolean = false,
    val name: String = "",
    val photoUrl: String = "",
    val myBookings: PersistentList<Booking> = persistentListOf(),
    val days: PersistentList<Day> = persistentListOf(),
    val selectedDay: Int = 0,
    val alert: AlertKind? = null,
    val error: String? = null,
) {
    data class Booking(val date: String, val place: String)
    data class Day(val date: String, val places: PersistentList<Place>)
    data class Place(val id: Long, val name: String)
}
