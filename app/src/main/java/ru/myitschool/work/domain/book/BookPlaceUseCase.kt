package ru.myitschool.work.domain.book

import ru.myitschool.work.data.repo.BookRepository

class BookPlaceUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(date: String, placeId: Long): Result<Unit> = repository.book(date, placeId)
}
