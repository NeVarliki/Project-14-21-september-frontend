package ru.myitschool.work.domain.book.entities

data class BookingData(
    val date: String,
    val places: List<Place>
) {
    data class Place(
        val id: Long,
        val name: String
    )
}
