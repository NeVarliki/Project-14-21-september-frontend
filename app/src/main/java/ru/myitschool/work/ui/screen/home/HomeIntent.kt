package ru.myitschool.work.ui.screen.home

import ru.myitschool.work.ui.components.HomeTab

sealed interface HomeIntent {
    data object Refresh : HomeIntent
    data object Logout : HomeIntent
    data class SelectTab(val tab: HomeTab) : HomeIntent
    data class SelectDay(val index: Int) : HomeIntent
    data class BookPlace(val placeId: Long, val placeName: String) : HomeIntent
    data class BookRoom(val placeId: Long, val placeName: String) : HomeIntent
    data object AskFree : HomeIntent
    data object ConfirmFree : HomeIntent
    data object DismissAlert : HomeIntent
}
