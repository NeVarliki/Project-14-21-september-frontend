package ru.myitschool.work.domain.book

import ru.myitschool.work.data.repo.BookRepository
import ru.myitschool.work.domain.book.entities.BookingData
import ru.myitschool.work.domain.book.entities.Cached

class GetFreePlacesUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<Cached<List<BookingData>>> = repository.getFreePlaces()
}
