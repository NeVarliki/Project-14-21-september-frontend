package ru.myitschool.work.domain.book

import ru.myitschool.work.data.repo.BookRepository

class FreeBookingUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.free()
}
