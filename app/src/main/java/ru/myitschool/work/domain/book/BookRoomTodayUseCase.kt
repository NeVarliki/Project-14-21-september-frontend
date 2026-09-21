package ru.myitschool.work.domain.book

import ru.myitschool.work.data.repo.BookRepository

class BookRoomTodayUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(placeId: Long): Result<Unit> = repository.bookRoomToday(placeId)
}
